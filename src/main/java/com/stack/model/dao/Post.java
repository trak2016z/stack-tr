package com.stack.model.dao;

import com.stack.model.DomainContext;
import com.stack.model.entities.CommentariesEntity;
import com.stack.model.entities.PostsEntity;
import com.stack.model.entities.VotesEntity;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Post extends Common {

    PostsEntity entity;

    public Post(){
        session = DomainContext.openSession();
        this.entity = new PostsEntity();
    }

    public Post(String id){
        session = DomainContext.openSession();
        findById(id);
    }

    public Post(Session session){
        this.session = session;
    }

    Post(PostsEntity entity, Session session){
        this.entity = entity;
        this.session = session;
    }

    public Post(User current) {
        this.entity = new PostsEntity();
        session = current.session;
        setOwner(current);
    }

    public Post(PostsEntity e) {
        this.entity = e;
    }

    public void findById(String s) {
        entity = session.get(PostsEntity.class, Integer.parseInt(s));
    }

    public void save() {
        try{
            session.persist(entity);
            session.flush();
        }
        finally {
            DomainContext.closeSession(session);
        }
    }

    public void delete() {
        session.delete(entity);
    }

    @SuppressWarnings("unchecked")
    public static List<Post> findAll(Session session) {
        List<Post> result = new ArrayList<>();
        List<PostsEntity> entities = (List<PostsEntity>) session.createQuery("from PostsEntity where posttype = 'Q' order by id desc").list();
        for(PostsEntity entity : entities){
            result.add(new Post(entity, session));
        }
        return result;
    }

    public int getId() {
        return entity.getId();
    }
    public String getType() {
        return entity.getPosttype();
    }

    public void setType(String posttype) {
        entity.setPosttype(posttype);
    }

    public String getCreationDate() {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(entity.getCreationdate());
    }

    public void setCreationDate(Timestamp creationdate) {
        entity.setCreationdate(creationdate);
    }

    public Integer getScore() {
        return entity.getScore();
    }

    public void setScore(Integer score) {
        entity.setScore(score);
    }

    public String getBody() {
        return entity.getBody();
    }

    public void setBody(String body) { entity.setBody(body); }

    public Timestamp getLastEditDate() {
        return entity.getLasteditdate();
    }

    public void setLastEditDate(Timestamp lasteditdate) {
        entity.setLasteditdate(lasteditdate);
    }

    public String getTitle() {
        return entity.getTitle();
    }

    public void setTitle(String title) {
        entity.setTitle(title);
    }

    public Timestamp getCloseDate() {
        return entity.getCloseddate();
    }

    public void setCloseDate(Timestamp closeddate) {
        entity.setCloseddate(closeddate);
    }

    public String getCloseReason() {
        return entity.getClosedreason();
    }

    public void setCloseReason(String closedreason) {
        entity.setClosedreason(closedreason);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post that = (Post) o;

        return entity.getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return entity.hashCode();
    }

    public User getOwner() {
        return new User(entity.getUsersByOwneruserid(), session);
    }

    public void setOwner(User user) {
        this.entity.setUsersByOwneruserid(user.entity);
    }

    public Collection<Comment> getCommentaries() {
        Collection<Comment> result = new ArrayList<>();

        for(CommentariesEntity e : entity.getCommentariesById()){
            result.add(new Comment(e));
        }


        return result;
    }

    public Collection<Post> getAnswers() {
        Collection<Post> result = new ArrayList<>();

        for(PostsEntity e : entity.getPostsesById_0()){
            result.add(new Post(e));
        }

        return result;
    }

    public List<Vote> getVotes() {
        List<Vote> result = new ArrayList<>();
        Query query = session.createQuery("from VotesEntity where postid=:id");
        query.setParameter("id", entity.getId());
        List<?> votes = query.list();
        if (votes != null) {
            for (Object vote : votes) {
                result.add(new Vote((VotesEntity) vote, session));
            }
        }
        return result;
    }

    public Comment addComment(String body) {
        CommentariesEntity comment = new CommentariesEntity();
        comment.setBody(body);
        comment.setCreationdate(new Timestamp(System.currentTimeMillis()));
        comment.setPostsByPostid(entity);
        comment.setUsersByUserid(User.getCurrentUser(session).entity);
        entity.getCommentariesById().add(comment);

        return new Comment(comment);
    }

    public boolean wasVotedByUser(User currUser) {
        boolean result = false;
        List<Vote> votes = getVotes();
        for(Vote vote : votes){
            if(vote.getUser().getId() ==  currUser.getId()){
                result = true;
                break;
            }
        }
        return result;
    }

    public void incScore() {
        entity.setScore(entity.getScore() + 1);
    }

    public void setParent(String postid) {
        entity.setPostsByParentid(session.get(PostsEntity.class, Integer.parseInt(postid)));
    }
}
