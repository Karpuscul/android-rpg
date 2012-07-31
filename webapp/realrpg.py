#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import json
import os
import cgi
import datetime
import webapp2

from google.appengine.ext import db
from google.appengine.api import users
from google.appengine.ext.webapp import template
from google.appengine.ext.webapp.util import login_required

def isfloat( s ):
    try:
        float( s )
        return True
    except:
        return False

class Aim( db.Model ):
    author = db.UserProperty()
    user = db.StringProperty()
    latitude = db.FloatProperty()
    longitude = db.FloatProperty()
    message = db.StringProperty( multiline = True )
    completed = db.BooleanProperty()

    def to_string( self ):
        data = { "id": self.key().id(), "user": self.user, "latitude": self.latitude, "longitude": self.longitude, "message": self.message }
        return json.dumps( data )


class MainPage(webapp2.RequestHandler):
    @login_required
    def get(self):
        path = os.path.join(os.path.dirname(__file__), 'index.html')
        template_values = {}
        self.response.out.write(template.render(path, template_values))


class SetPoint(webapp2.RequestHandler):
    def get(self):
        for aim in Aim.all().filter( "user =", self.request.get( "user" ) ):
            aim.completed = True
            aim.put()

        try:
            if not self.request.get( "user" ):
                raise Exception( "User is required" )
            if not isfloat( self.request.get( "lat" ) ):
                raise Exception( "Latitude is required" )
            if not isfloat( self.request.get( "lon" ) ):
                raise Exception( "Longitude is required" )
            if not self.request.get( "message" ):
                raise Exception( "Message is required" )

            aim = Aim()
            aim.author = users.get_current_user()
            aim.user = self.request.get( "user" )
            aim.latitude = float( self.request.get( "lat" ) )
            aim.longitude = float( self.request.get( "lon" ) )
            aim.message = self.request.get( "message" )
            aim.completed = False

            aim.put()
        except Exception, e:
            path = os.path.join(os.path.dirname(__file__), 'index.html')
            template_values = { "msg": unicode( e ), "user": self.request.get( "user" ), "lat": self.request.get( "lat" ), 
                "lon": self.request.get( "lon" ), "message": self.request.get( "message" ) }
            self.response.out.write(template.render(path, template_values))
            return

        path = os.path.join(os.path.dirname(__file__), 'pointadded.html')
        template_values = {}
        self.response.out.write(template.render(path, template_values))

class GetPoint(webapp2.RequestHandler):
    def get(self):
        aim = Aim.all().filter( "user =", self.request.get( "user" ) ).filter( "completed =", False )
        if aim.count():
            self.response.out.write( aim[ 0 ].to_string() )
        else:
            self.response.out.write( "EMPTY" )

class CompletePoint(webapp2.RequestHandler):
    def get(self):
        for aim in Aim.all().filter( "user =", self.request.get( "user" ) ):
            aim.completed = True
            aim.put()


app = webapp2.WSGIApplication([
    ( '/', MainPage ),
    ( '/setpoint', SetPoint ),
    ( '/getpoint', GetPoint ),
    ( '/completepoint', CompletePoint ),
], debug=True)

