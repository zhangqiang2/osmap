#!/usr/bin/python2

"""
    project.py
    ~~~~~~~~~~~~~~
    implemention of extract project info from openhub

    :copyright: (c) 2017 by wang.mingyu.
"""

import sys
import time
import subprocess
import json
import xmltodict
import requests
import re
from os import path
from multiprocessing import Process
from logger import logger
import dbtool
import urltoproject

APIKEY = '498aa38ede45927e2b194d90b554be82dcad48057e42e6d4b0bf0b3a471319bd'

try:
    casper = subprocess.check_output('which casperjs', shell=True, stderr=subprocess.STDOUT).strip()
except subprocess.CalledProcessError:
    logger.error('casperjs is not found, please make sure it has been installed')
    sys.exit(1)

class Project:

    def __init__(self):
        self.id = None
        self.name = None
        self.updated_at = None
        self.description = None
        self.homepage_url = None
        self.download_url = None
        self.url = None
        self.licenses = None
        self.project_activity_index = None
        self.html_url = None
        self.locations = None
        self.foundation = None
        self.community = None
        self.communityurl = None

    def __str__(self):
        return json.dumps(self.__dict__)

def get_abs_file_path(filename):
    return path.abspath(path.join(path.dirname(__file__), filename))

def get_phantom_result(url):
    if not url:
        return None

    cmd = "%s %s --url=%s" % (casper, get_abs_file_path('openhub.js'), url)
    logger.info('begin to execute: ' +  cmd)
    p = subprocess.Popen(['/bin/sh', '-c', cmd], stdout=subprocess.PIPE, stderr=subprocess.PIPE)

    for line in iter(p.stdout.readline, ""):
        match = re.search('\[python-pipe\]\s*result:\s*(?P<result>{.*})', line)
        if match is not None:
            result_msg = match.group('result')
            logger.info('result: ' + result_msg)
            return json.loads(match.group('result'))

        match = re.search('\[python-pipe\]\s*error:\s*(?P<error>.*)', line)
        if match is not None:
            logger.error(match.group('error'))
            continue

        match = re.search('\[python-pipe\]\s*info:\s*(?P<info>.*)', line)
        if match is not None:
            logger.info(match.group('info'))
            continue

    while p.poll() is None:
        time.sleep(1)

    return None

def handle_project(p):
    project = Project()
    project.id = p['id']
    project.name = p['name']
    project.homepage_url = p['homepage_url']
    project.download_url = p['download_url']
    licenses = []
    if p['licenses']:
        p_licenses = p['licenses']['license']
        if type(p_licenses) == dict:
            licenses.append(p_licenses['name'])
        elif type(p_licenses) == list:
            for l in p_licenses:
                licenses.append(l['name'])

    project.licenses = ','.join(licenses)
    project.project_activity_index  = p['project_activity_index']['value']
    project.updated_at = p['updated_at']
    project.html_url = p['html_url']
    project.description = p['description']
    other_info = get_phantom_result(project.html_url)
    if other_info:
        project.locations = other_info['locations']
        project.foundation = other_info['foundation']
        project.community = other_info['community']
        project.communityurl = other_info['communityurl']

    dbtool.add_project(project)

def start_process(project):
    p = Process(target=handle_project, args=(project,))
    p.start()
    return p

def parse_search_result(xml):
    ret = xmltodict.parse(xml, xml_attribs=False, dict_constructor=dict)
    if not ret['response'].has_key('result'):
        return None

    result = []
    projects = ret['response']['result']['project']

    if type(projects) is dict:
        result.append(projects)
    else:
        for p in projects:
            result.append(p)

    return result

def fetch_project_infos(projects, sync=False):
    if not projects:
        return None

    processes = []
    for p in projects:
        processes.append(start_process(p))

    if sync:
        for p in processes:
            p.join()
        return None
    else:
        return projects[0]['name']


def search(content, sync=False):
    logger.info('Begin to search: ' + content)
    query = urltoproject.parse(content)
    logger.info('after url to project, search content is: ' + query)
    params = {
            'api_key': APIKEY,
            'query': query
            }
    r = requests.get('https://www.openhub.net/projects.xml', params=params)
    if r.status_code == 200:
        first_project = fetch_project_infos(parse_search_result(r.text), sync)
        logger.info('Successed to search: ' + query + ('' if sync else '   ...running in background'))
        if first_project:
            logger.info('Bese match project is: ' + first_project)
            return first_project
        else:
            logger.info('No bese match project')
            return ''
    else:
        logger.error('error occurs while searching openhub, status_code:'+ str(r.status_code))
        return None

def search_for_projects(content):
    logger.info('Begin to search for project: ' + content)
    query = urltoproject.parse(content)
    logger.info('after url to project, search content is: ' + query)
    params = {
            'api_key': APIKEY,
            'query': query
            }
    r = requests.get('https://www.openhub.net/projects.xml', params=params)
    if r.status_code == 200:
        projects = parse_search_result(r.text)
        logger.info('Successed to get all projects')
        return ','.join([ p['name'] for p in projects ])
    else:
        logger.error('error occurs while searching openhub, status_code:'+ str(r.status_code))
        return None

def start_update_process(projects):
    projects = dbtool.get_all_project()
    processes = []
    for idx, project in enumerate(projects):
        if idx % 10 == 0 and processes:
            for process in processes:
                process.join()
            processes = []
        processes.append(update_by_name(project))


def update_all():
    logger.info('Begin to update all projects ')
    # p = Process(target=start_update_process)
    # p.start()
    projects = dbtool.get_all_project()
    update_by_name(projects[0])

def update_by_name(prjname):
    logger.info('Begin to update project: ' + prjname)
    params = {
            'api_key': APIKEY,
            'query': prjname
            }
    r = requests.get('https://www.openhub.net/projects.xml', params=params)
    if r.status_code == 200:
        projects = parse_search_result(r.text)
        if not projects:
            return None

        for p in projects:
            if p['name'] == prjname:
                logger.info('Successed to update: ' + prjname)
                return start_process(p)
        return None
    else:
        logger.error('error occurs while searching openhub, status_code:'+ str(r.status_code))
        return None
    return None


if __name__ == '__main__':
    # pjs = search('openstack')
    # searchs = [
    # 'OpenStack',
    # 'Ansible',
    # 'Ceph',
    # 'Cloud Foundry',
    # 'Docker',
    # 'Kubernetes',
    # 'OPNFV',
    # 'OpenShift',
    # 'Open vSwitch',
    # 'Python',
    # 'Packaging']
    # for s in searchs:
        # search(s, True)
    update_by_name('ceph-client-standalone')
