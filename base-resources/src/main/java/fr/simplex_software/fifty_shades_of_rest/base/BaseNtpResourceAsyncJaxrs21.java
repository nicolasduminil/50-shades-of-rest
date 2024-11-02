package fr.simplex_software.fifty_shades_of_rest.base;

import jakarta.annotation.*;
import jakarta.ws.rs.*;
import org.apache.commons.net.ntp.*;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.time.*;
import java.time.format.*;
import java.util.concurrent.*;

public class BaseNtpResourceAsyncJaxrs21
{
  private static final String TIME_SERVER = "time.google.com";
  private static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";
  private final NTPUDPClient ntpClient = new NTPUDPClient();
  private static InetAddress inetAddress;
  private static final Logger LOG = LoggerFactory.getLogger(BaseNtpResourceAsyncJaxrs21.class);

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
  public CompletionStage<String> currentTime()
  {
    CompletableFuture<String> future = new CompletableFuture<>();
    try
    {
      long time = ntpClient.getTime(inetAddress).getMessage().getTransmitTimeStamp().getTime();
      future.complete(Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern(FMT)));
    } catch (IOException e)
    {
      LOG.error("### BaseNtpResourceAsyncJaxrs21.currentTime(): Unexpected exception {}", e.getMessage());
    }
    return future;
  }

  @GET
  @Path("{zoneId}")
  public CompletionStage<String> zonedTime(@PathParam("zoneId") String zoneId)
  {
    CompletableFuture<String> future = new CompletableFuture<>();
    try
    {
      long time = ntpClient.getTime(inetAddress).getMessage().getTransmitTimeStamp().getTime();
      future.complete(Instant.ofEpochMilli(time)
        .atZone(ZoneId.of(URLDecoder.decode(zoneId, StandardCharsets.UTF_8)))
        .format(DateTimeFormatter.ofPattern(FMT)));
    } catch (IOException e)
    {
      LOG.error("### BaseNtpResourceAsyncJaxrs21.zonedTime(): Unexpected exception {}", e.getMessage());
    }
    return future;
  }
}
