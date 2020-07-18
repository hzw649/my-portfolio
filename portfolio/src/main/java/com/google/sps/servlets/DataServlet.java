// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.appengine.api.users.UserServiceFactory;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("Comments").addSort("timestamp", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        ArrayList<Comment> comments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            long id = entity.getKey().getId();
            String title = (String) entity.getProperty("title");
            String email = (String) entity.getProperty("email");
            long timestamp = (long) entity.getProperty("timestamp");

            Comment comment  = new Comment(id, title, timestamp, email);
            comments.add(comment);
        }

        String json = new Gson().toJson(comments);
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("comment");
        long timestamp = System.currentTimeMillis();
        String email = UserServiceFactory.getUserService().getCurrentUser().getEmail();

        Entity taskEntity = new Entity("Comments");
        taskEntity.setProperty("title", title);
        taskEntity.setProperty("timestamp", timestamp);

        taskEntity.setProperty("email", email);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(taskEntity);

        response.sendRedirect("/index.html");
    }
}


final class Comment {
    long id, timestamp;
    String title, email;
    Comment(long _id, String _title, long _ts, String _email){
        id = _id;
        title = _title;
        timestamp = _ts;
        email = _email;
    }
}
