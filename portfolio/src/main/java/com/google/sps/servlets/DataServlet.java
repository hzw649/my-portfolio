package com.google.sps.servlets;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

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
            long timestamp = (long) entity.getProperty("timestamp");

            Comment comment  = new Comment(id, title, timestamp);
            comments.add(comment);
        }


        String json = new Gson().toJson(comments);
        response.setContentType("application/json;");
        response.getWriter().println(json);
        //    System.out.println("GET:"+json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("comment");
        long timestamp = System.currentTimeMillis();

        Entity taskEntity = new Entity("Comments");
        taskEntity.setProperty("title", title);
        taskEntity.setProperty("timestamp", timestamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(taskEntity);

        response.sendRedirect("/index.html");
    }
}


final class Comment {
    long id, timestamp;
    String title;
    Comment(long _id, String _title, long _ts){
        id = _id;
        title = _title;
        timestamp = _ts;
    }
}
