/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Myapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * REST Web Service
 *
 * @author 1894219
 */
@Path("cegep")
public class GenericResource {
    
    static Connection conn = null;
    static Statement stm = null;
    static ResultSet rs = null;
    static JSONObject mainObject = new JSONObject();
    Date date = new Date();
    long time = date.getTime();
    static String countryId, countryName, regionId;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    /**
     * Retrieves representation of an instance of Myapp.GenericResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    public static Connection getConnection() {

        try {

            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "hr", "inf5180");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return conn;
    }
    
    public static void closeConnection() {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                /* ignored */
            }
        }
        if (stm != null) {
            try {
                stm.close();
            } catch (SQLException e) {
                /* ignored */
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                /* ignored */
            }
        }
    }
    
    @GET
    @Path("getsingle&{country_id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSingle(@PathParam("country_id")String country_id) {
        mainObject.clear();
        conn = getConnection();
        
        String sql = "SELECT * FROM COUNTRIES WHERE COUNTRY_ID='"+ country_id.toUpperCase() +"'";
        
        if (conn != null) {
            try {
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);

                rs = stm.executeQuery(sql);
                
                if(rs.next() == true){
                    mainObject.accumulate("Status", "OK");
                    mainObject.accumulate("TimeStamp", time);
                    do {
           
                        countryId = rs.getString("country_id");
                        countryName = rs.getString("country_name");
                        regionId = rs.getString("region_id");
                        mainObject.accumulate("CountryId", countryId);
                        mainObject.accumulate("CountryName", countryName);
                        mainObject.accumulate("RegionId", regionId);
                    }while (rs.next());
                }else{
                    
                    mainObject.accumulate("Status", "Error");
                    mainObject.accumulate("TimeStamp", time);
                    mainObject.accumulate("Message", "Record Not Found");
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            mainObject.accumulate("Status", "Error");
            mainObject.accumulate("TimeStamp", time);
            mainObject.accumulate("Message", "Connection Error");
            
        }
        return mainObject.toString();
    }
    
    @GET
    @Path("getlist")
    @Produces(MediaType.TEXT_PLAIN)
    public String getList() {
        
        mainObject.clear();
        JSONArray mainarrList = new JSONArray();
        JSONObject singleobjList = new JSONObject();

        conn = getConnection();

        if (conn != null) {
            try {
                String sql = "SELECT COUNTRY_ID,COUNTRY_NAME,REGION_ID FROM COUNTRIES";
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);

                rs = stm.executeQuery(sql);

                if(rs.next() == true){
                    mainObject.accumulate("Status", "OK");
                    mainObject.accumulate("TimeStamp", time);
                    do {

                        countryId = rs.getString("country_id");
                        countryName = rs.getString("country_name");
                        regionId = rs.getString("region_id");
                        singleobjList.accumulate("CountryId", countryId);
                        singleobjList.accumulate("CountryName", countryName);
                        singleobjList.accumulate("RegionId", regionId);
                        mainarrList.add(singleobjList);
                        singleobjList.clear();

                    }while (rs.next());
                    mainObject.accumulate("Countries", mainarrList);
                }else{
                    
                    mainObject.accumulate("Status", "Error");
                    mainObject.accumulate("TimeStamp", time);
                    mainObject.accumulate("Message", "No Records Found");
                    
                }
            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            mainObject.accumulate("Status", "Error");
            mainObject.accumulate("TimeStamp", time);
            mainObject.accumulate("Message", "Connection Error");
        }
        return mainObject.toString();
    }
    
    @GET
    @Path("insertdata&{country_id}&{country_name}&{region_id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String insertData(@PathParam("country_id")String country_id,@PathParam("country_name")String country_name,@PathParam("region_id")String region_id) {
    
        mainObject.clear();
        conn = getConnection();
        
        String sql = "INSERT INTO COUNTRIES(COUNTRY_ID,COUNTRY_NAME,REGION_ID) VALUES('"+ country_id.toUpperCase() +"','"+ country_name +"','"+ region_id +"')";
        
        if (conn != null) {
            try {
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);

                if (i > 0) {
                    mainObject.accumulate("Status", "OK");
                    mainObject.accumulate("TimeStamp", time);
                    mainObject.accumulate("Message", "Record inserted");

                } else {
                    mainObject.accumulate("Status", "Error");
                    mainObject.accumulate("TimeStamp", time);
                    mainObject.accumulate("Message", "Record Not inserted");
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            mainObject.accumulate("Status", "Error");
            mainObject.accumulate("TimeStamp", time);
            mainObject.accumulate("Message", "Connection Error");
        }
        return mainObject.toString();
    }
    
    @GET
    @Path("updatedata&{country_id}&{country_name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String updateData(@PathParam("country_id")String country_id,@PathParam("country_name")String country_name) {
    
        mainObject.clear();
        
        conn = getConnection();
        
        String sql = "UPDATE COUNTRIES SET COUNTRY_NAME='"+ country_name +"' WHERE COUNTRY_ID='"+ country_id.toUpperCase() +"'";
        
        if (conn != null) {
            try {
                stm = conn.createStatement();

                int i = stm.executeUpdate(sql);

                if (i > 0) {
                    mainObject.accumulate("Status", "OK");
                    mainObject.accumulate("TimeStamp", time);
                    mainObject.accumulate("Message", "Record Updated");
                } else {
                    mainObject.accumulate("Status", "Error");
                    mainObject.accumulate("TimeStamp", time);
                    mainObject.accumulate("Message", "Record Not Updated");
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            mainObject.accumulate("Status", "Error");
            mainObject.accumulate("TimeStamp", time);
            mainObject.accumulate("Message", "Connection Error");
        }
        return mainObject.toString();
    }
    
    @GET
    @Path("deletedata&{country_id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteData(@PathParam("country_id")String country_id) {
    
        mainObject.clear();
        
        conn = getConnection();
        
        String sql = "DELETE FROM COUNTRIES WHERE COUNTRY_ID = '"+ country_id.toUpperCase() +"'";
        
        if (conn != null) {
            try {
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);
                if (i > 0) {
                    mainObject.accumulate("Status", "OK");
                    mainObject.accumulate("TimeStamp", time);
                    mainObject.accumulate("Message", "Record Deleted");
                } else {
                    mainObject.accumulate("Status", "Error");
                    mainObject.accumulate("TimeStamp", time);
                    mainObject.accumulate("Message", "Record not Deleted");
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            mainObject.accumulate("Status", "Error");
            mainObject.accumulate("TimeStamp", time);
            mainObject.accumulate("Message", "Connection Error");
        }
        
        return mainObject.toString();
    }
}
