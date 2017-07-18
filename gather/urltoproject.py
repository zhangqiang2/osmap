#!/usr/bin/python2
# -*- coding: utf-8 -*-
"""
    urltoproject.py
    ~~~~~~~~~~~~~~
    extract project name info from url

    :copyright: (c) 2017 by wang.mingyu.
"""

import sys

def parse(url):
    if url.startswith('https://') or url.startswith('http://') or url.startswith('www.'):
        info = url.replace('https://','').replace('http://','').replace('www.','')
        if info.startswith('github.com'):
            return info.split('/')[2]
        if info.startswith('openhub.net/p/'):
            return info.split('/')[2]
        if info.startswith('sourceforge.net/projects/'):
            return info.split('/')[2]
        if info.startswith('projects.apache.org/project.html?'):
            return info.split('?')[1]


        domin = info.split('/')[0].replace('.org','').replace('.com','').replace('.net','')
        return domin.replace('.', ' ')

    else:
        return url.split('/')[0].replace('.', ' ')

if __name__ == '__main__':
    assert parse('https://github.com/IonicaBizau/git-stats') == 'git-stats'
    assert parse('https://www.openhub.net/p/openshift-origin') == 'openshift-origin'
    assert parse('https://sourceforge.net/projects/gitstats/') == 'gitstats'
    assert parse('https://projects.apache.org/project.html?couchdb') == 'couchdb'
    assert parse('https://www.openstack.org/') == 'openstack'
    assert parse('http://couchdb.apache.org/') == 'couchdb apache'
    assert parse('https://www.jeedom.com/site/fr/') == 'jeedom'
    assert parse('http://www.compiz.org/') == 'compiz'
    assert parse('http://docs.casperjs.org/en/latest/installation.html') == 'docs casperjs'
    assert parse('hadoop.apache') == 'hadoop apache'
    print('finish!')
