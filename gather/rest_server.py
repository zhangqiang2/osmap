#!/usr/bin/python2
# -*- coding: utf-8 -*-
"""
    osmap RESTful server implemented using the Flask-RESTful extension.

    :copyright: (c) 2017 by wang.mingyu.
"""

import flask
from flask import Flask
from flask_restful import Api
from flask_cors import CORS
from flask import request
from flask_restful import Resource, reqparse
import json
import project


app = Flask(__name__, static_url_path="")
CORS(app)
api = Api(app)

class OpenHub(Resource):
    def __init__(self):
        self.reqparser = reqparse.RequestParser(bundle_errors=True)
        self.reqparser.add_argument('q', required=True)

    def parse_post(self):
        args = self.reqparser.parse_args()
        query = args['q']
        return query

    def post(self):
        try:
            query = self.parse_post()
            if query:
                return project.search(query)
            else:
                return None
        except RuntimeError as e:
            return e.message, 404

class OpenHubProjects(Resource):
    def __init__(self):
        self.reqparser = reqparse.RequestParser(bundle_errors=True)
        self.reqparser.add_argument('q', required=True)

    def parse_post(self):
        args = self.reqparser.parse_args()
        query = args['q']
        return query

    def post(self):
        try:
            query = self.parse_post()
            if query:
                return project.search_for_projects(query)
            else:
                return None
        except RuntimeError as e:
            return e.message, 404

class UpdateAPI(Resource):
    def __init__(self):
        self.reqparser = reqparse.RequestParser(bundle_errors=True)
        self.reqparser.add_argument('q', default=None)

    def parse_post(self):
        args = self.reqparser.parse_args()
        query = args['q']
        return query

    def post(self):
        try:
            query = self.parse_post()
            if query:
                project.update_by_name(query)
                return query
            else:
                project.update_all()
                return '*'
        except RuntimeError as e:
            return e.message, 404

class StaticAPI(Resource):
    def get(self):
        help = {}
        help['search'] = '/rest/openhub?q=search'
        help['get all projects'] = '/rest/openhub/projects?q=search'
        help['update one project'] = '/rest/update?q=prjname'
        help['update all projects'] = '/rest/update'
        return flask.jsonify(help)

api.add_resource(StaticAPI, '/', '/rest')
api.add_resource(OpenHub, '/rest/openhub')
api.add_resource(OpenHubProjects, '/rest/openhub/projects')
api.add_resource(UpdateAPI, '/rest/update')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, threaded=True)
    # app.run(host='0.0.0.0', port=5000, debug=True, threaded=True)
