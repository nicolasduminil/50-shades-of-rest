package fr.simplex_software.fifty_shades_of_rest.base;

import jakarta.annotation.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.*;
import org.apache.commons.net.ntp.*;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.time.*;
import java.time.format.*;
import java.util.concurrent.*;

public class BaseNtpResourceAsync
{
  private static final String TIME_SERVER = "time.google.com";
  private static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";
  private final NTPUDPClient ntpClient = new NTPUDPClient();
  private static InetAddress inetAddress;
  private static final Logger LOG = LoggerFactory.getLogger(BaseNtpResourceAsync.class);
  @Inject
  private Executor executor;

  @PostConstruct
  public void postConstruct() throws Exception
  {
    inetAddress = InetAddress.getByName(TIME_SERVER);
    ntpClient.setDefaultTimeout(5000);
    ntpClient.open();
    LOG.info(">>> CurrentTimeNtpResource(): The NTP client is ready to serve requests at {}",
      inetAddress);
  }

  @GET
  public void currentTime(@Suspended AsyncResponse ar) throws IOException
  {
    executor.execute(() ->
    {
      try
      {
        long time = ntpClient.getTime(inetAddress).getMessage().getTransmitTimeStamp().getTime();
        String formattedTime = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault())
          .format(DateTimeFormatter.ofPattern(FMT));
        ar.resume(formattedTime);
      }
      catch (IOException e)
      {
        ar.resume(e);
      }
    });
  }

  @GET
  @Path("{zoneId}")
  public void zonedTime(@PathParam("zoneId") String zoneId, @Suspended AsyncResponse ar) throws IOException
  {
    executor.execute(() ->
    {
      try
      {
        long time = ntpClient.getTime(inetAddress).getMessage().getTransmitTimeStamp().getTime();
        String formattedTime = Instant.ofEpochMilli(time).atZone(ZoneId.of(URLDecoder.decode(zoneId, StandardCharsets.UTF_8)))
          .format(DateTimeFormatter.ofPattern(FMT));
        ar.resume(formattedTime);
      }
      catch (IOException e)
      {
        ar.resume(e);
      }
    });
  }
}
