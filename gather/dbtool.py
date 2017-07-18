#!/usr/bin/python2
# -*- coding: utf-8 -*-
"""
    dbtool.py
    ~~~~~~~~~~~~~~
    implemention of mysql db operation

    :copyright: (c) 2017 by wang.mingyu.
"""

import sys
from mysql import connector
from logger import logger

conn = connector.connect(user='root', password='U_tywg_2013', host='10.43.33.136', database='daptracker')
cur = conn.cursor()

def query_all(*args):
    try:
        cur.execute(*args)
        return [i for i in cur]
    finally:
        #conn.close()
        pass

def query(*args):
    try:
        cur.execute(*args)
        return cur.fetchone()
    finally:
        #conn.close()
        pass

def execute(*args):
    try:
        cur.execute(*args)
        conn.commit()
    except connector.errors.ProgrammingError as e:
        print( 'error occurs')
        print( args[0])
        print( args[1])
        print( e)
        sys.exit(1)
    finally:
        #conn.close()
        pass


def insert(table, fields, values):
    execute( "insert into {table} ({fields}) values ({values})".format(
            table=table, fields=','.join(fields), values=",".join(["%s"]*len(fields))
            ), tuple(values))

def update(table, fields, pk, values):
    execute( "update {table} set {keyvalue} where {pk}=%s".format(
            table=table, keyvalue= ', '.join([ i+'=%s' for i in fields ]), pk=pk
            ), tuple(values))

project_fields = ['prjname', 'prjurl', 'downloadurl', 'licensename', 'updatetime', 'vitality', 'orgsourceaddr', 'foundationname', 'communityname', 'communityurl']
extra_fields = ['orgsourceaddr', 'foundationname', 'communityname', 'communityurl']
def get_values(project, extra=None):
    result = []
    result.append(project.name)
    result.append(project.homepage_url)
    result.append(project.download_url)
    result.append(project.licenses)
    result.append(project.updated_at)
    result.append(project.project_activity_index)
    if extra:
        result.append(project.locations if project.locations else extra[0] )
        result.append(project.foundation if project.foundation else extra[1] )
        result.append(project.community if project.community else extra[2] )
        result.append(project.communityurl if project.communityurl else extra[3] )
    else:
        result.append(project.locations)
        result.append(project.foundation)
        result.append(project.community)
        result.append(project.communityurl)


    return result

def add_project(project):
    name = project.name
    if isProjectExist(name):
        update_project(name, get_values(project, query_by_name(name)))
    else:
        insert('prjbaseinfo', project_fields, get_values(project))

def update_project(name, values):
    values.append(name)
    update('prjbaseinfo', project_fields, 'prjname', values)

def query_by_name(name):
    ret = query('select {} from prjbaseinfo where prjname=%s'.format(','.join(extra_fields)), (name,))
    if ret:
        return ret
    else:
        return None

def query_for_one(sql, params):
    ret = query(sql, params)
    if ret:
        return ret[0]
    else:
        return None

def isProjectExist(name):
    return query_for_one("SELECT count(prjname) FROM prjbaseinfo where prjname=%s", (name,))

def get_all_project():
    ret = query_all('select prjname from prjbaseinfo')
    if ret:
        return [ str(i[0]) for i in ret ]
    else:
        return []

if __name__ == '__main__':
    print(get_all_project())
    # for p in get_all_project():
        # print(p)
