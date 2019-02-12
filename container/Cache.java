package tokyo.nikokingames.shotbow.vpnblocker.container;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Scanner;

public final class Cache
{
  private int cache_time = 1;
  private String cache_path = "cache/";
  private String cache_extension = ".cache";
  
  public Cache() {}
  
  public Cache(String path)
  {
    set_cache_path(path);
  }
  
  public int get_cache_time()
  {
    return this.cache_time;
  }
  
  public String get_cache_path()
  {
    return this.cache_path;
  }
  
  public String get_cache_extension()
  {
    return this.cache_extension;
  }
  
  public void set_cache_time(int days)
  {
    this.cache_time = days;
  }
  
  public void set_cache_path(String path)
  {
    this.cache_path = path;
    if (!new File(this.cache_path).isDirectory()) {
      new File(this.cache_path).mkdirs();
    }
  }
  
  public void set_cache_extension(String ext)
  {
    this.cache_extension = ext;
  }
  
  public boolean is_cached(String label)
  {
    String filename = String.valueOf(this.cache_path) + safe_filename(label) + this.cache_extension;
    File file = new File(filename);
    long diff = new Date().getTime() - file.lastModified();
    
    return (file.exists()) && (diff <= this.cache_time * 24L * 60L * 60L * 1000L);
  }
  
  public String get_cache(String label)
  {
    if (is_cached(label))
    {
      String filename = String.valueOf(this.cache_path) + safe_filename(label) + this.cache_extension;
      try
      {
        Scanner reader = new Scanner(new File(filename)).useDelimiter("\\Z");Throwable localThrowable3 = null;
        try
        {
          String data = reader.next();
          reader.close();
          return data;
        }
        catch (Throwable localThrowable4)
        {
          localThrowable3 = localThrowable4;throw localThrowable4;
        }
        finally
        {
          if (reader != null) {
            if (localThrowable3 != null) {
              try
              {
                reader.close();
              }
              catch (Throwable localThrowable2)
              {
                localThrowable3.addSuppressed(localThrowable2);
              }
            } else {
              reader.close();
            }
          }
        }
      }
      catch (FileNotFoundException e)
      {
        return null;
      }
    }
	return label;
  }
  
  public void set_cache(String label, String data)
  {
    try
    {
      Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.cache_path + safe_filename(label) + this.cache_extension), "utf-8"));Throwable localThrowable3 = null;
      try
      {
        writer.write(data);
        writer.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable3 = localThrowable1;throw localThrowable1;
      }
      finally
      {
        if (writer != null) {
          if (localThrowable3 != null) {
            try
            {
              writer.close();
            }
            catch (Throwable localThrowable2)
            {
              localThrowable3.addSuppressed(localThrowable2);
            }
          } else {
            writer.close();
          }
        }
      }
    }
    catch (IOException ex)
    {
      System.out.println(ex.getMessage());
    }
  }
  
  public String get_data(String label, String url)
    throws MalformedURLException, IOException
  {
    String data = null;
    if (get_cache(label) != null)
    {
      data = get_cache(label);
      return data;
    }
    data = grab_url(url);
    set_cache(label, data);
    return data;
  }
  
  public String grab_url(String url, int timeout, String userAgent)
    throws MalformedURLException, IOException
  {
    StringBuilder response = new StringBuilder();
    URL website = new URL(url);
    URLConnection connection = website.openConnection();
    connection.setConnectTimeout(timeout);
    connection.setRequestProperty("User-Agent", userAgent);
    
    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));Throwable localThrowable3 = null;
    try
    {
      while ((url = in.readLine()) != null) {
        response.append(url);
      }
      in.close();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable3 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (in != null) {
        if (localThrowable3 != null) {
          try
          {
            in.close();
          }
          catch (Throwable localThrowable2)
          {
            localThrowable3.addSuppressed(localThrowable2);
          }
        } else {
          in.close();
        }
      }
    }
    return response.toString();
  }
  
  public String grab_url(String url)
    throws MalformedURLException, IOException
  {
    StringBuilder response = new StringBuilder();
    URL website = new URL(url);
    URLConnection connection = website.openConnection();
    connection.setConnectTimeout(5000);
    
    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));Throwable localThrowable3 = null;
    try
    {
      while ((url = in.readLine()) != null) {
        response.append(url);
      }
      in.close();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable3 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (in != null) {
        if (localThrowable3 != null) {
          try
          {
            in.close();
          }
          catch (Throwable localThrowable2)
          {
            localThrowable3.addSuppressed(localThrowable2);
          }
        } else {
          in.close();
        }
      }
    }
    return response.toString();
  }
  
  public void clearCache()
  {
    for (File file : new File(this.cache_path).listFiles()) {
      file.delete();
    }
  }
  
  public void clearCache(String label)
    throws FileNotFoundException
  {
    String filename = String.valueOf(this.cache_path) + safe_filename(label) + this.cache_extension;
    File file = new File(filename);
    if (file.exists()) {
      file.delete();
    } else {
      throw new FileNotFoundException();
    }
  }
  
  public int get_total_cached()
  {
    return new File(this.cache_path).listFiles().length;
  }
  
  private String safe_filename(String filename)
  {
    return filename.replaceAll("/[^0-9a-z\\.\\_\\-]/i", "");
  }
}