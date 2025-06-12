package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.Filters.Filter;
import org.ja.model.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsersDao {
    private BasicDataSource dataSource;
    private long cnt=0;
    public UsersDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void insertUser(User user) throws SQLException {
        String sql="INSERT INTO users ( password_hashed, username, registration_date, user_photo, user_status) VALUES (?,?, ?, ?, ?);";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement preparedStatement =
                    c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getPasswordHashed());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setString(3, user.getRegistrDate());
            preparedStatement.setString(4, user.getPhoto());
            preparedStatement.setString(5, user.getStatus());

            preparedStatement.execute();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                long newId = keys.getLong(1);
                user.setId(newId); // if you want to store it in your object
                cnt++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void removeUserById(long id) {
        String sql = "DELETE FROM users WHERE user_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setLong(1, id);

            preparedStatement.execute();
            cnt--;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void removeUserByName(String name) {
        String sql = "DELETE FROM users WHERE user_name=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, name);

            preparedStatement.execute();
            cnt--;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public User getUserById(int id) {
        String sql="SELECT * FROM users WHERE user_id=?";
        try (Connection c=dataSource.getConnection()){
            PreparedStatement st=c.prepareStatement(sql);
            st.setLong(1, id);
            ResultSet rs=st.executeQuery();
            if(rs.next()){
                long nid=rs.getLong("user_id");
                String nUserName=rs.getString("username");
                String nRegistrDate=rs.getString("registration_date");
                String nPhoto=rs.getString("user_photo");
                String nStatus=rs.getString("user_status");
                String nPasswordHashed=rs.getString("password_hashed");
                User res=new User(nid, nUserName,nPasswordHashed, nRegistrDate, nPhoto, nStatus);
                return res;
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return null;
    }
    public User getUserByUsername(String username) {
        String sql="SELECT * FROM users WHERE username=?";
        try (Connection c=dataSource.getConnection()){
            PreparedStatement st=c.prepareStatement(sql);
            st.setString(1, username);
            ResultSet rs=st.executeQuery();
            if(rs.next()){
                long nid=rs.getLong("user_id");
                String nUserName=rs.getString("username");
                String nRegistrDate=rs.getString("registration_date");
                String nPhoto=rs.getString("user_photo");
                String nStatus=rs.getString("user_status");
                String nPasswordHashed=rs.getString("password_hashed");
                User res=new User(nid, nUserName,nPasswordHashed, nRegistrDate, nPhoto, nStatus);
                return res;
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return null;
    }
    public ArrayList<User> getUsersByFilter(Filter filter) {
        String sql="SELECT * FROM users WHERE "+filter.toString();
        ArrayList<User> ans=new ArrayList<>();
        try (Connection c=dataSource.getConnection()){
            PreparedStatement st=c.prepareStatement(sql);
            ResultSet rs=st.executeQuery();
            while(rs.next()){
                long nid=rs.getLong("user_id");
                String nUserName=rs.getString("username");
                String nRegistrDate=rs.getString("registration_date");
                String nPhoto=rs.getString("user_photo");
                String nStatus=rs.getString("user_status");
                String nPasswordHashed=rs.getString("password_hashed");
                User user=new User(nid, nUserName,nPasswordHashed, nRegistrDate, nPhoto, nStatus);
                ans.add(user);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return ans;
    }
    public long getCount(){
        return cnt;
    }
}
