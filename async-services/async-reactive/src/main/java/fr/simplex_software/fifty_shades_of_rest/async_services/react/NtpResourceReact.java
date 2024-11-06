package fr.simplex_software.fifty_shades_of_rest.async_services.react;

import io.smallrye.mutiny.*;
import jakarta.annotation.*;
import jakarta.enterprise.context.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.apache.commons.net.ntp.*;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.time.*;
import java.time.format.*;

@ApplicationScoped
@Path("ntp-rx")
@Produces(MediaType.TEXT_PLAIN)
public class NtpResourceReact
{
  private static final String TIME_SERVER = "time.google.com";
  private static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";
  private final NTPUDPClient ntpClient = new NTPUDPClient();
  private static InetAddress inetAddress;
  private static final Logger LOG = LoggerFactory.getLogger(NtpResourceReact.class);

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
  public Uni<String> currentTime() throws IOException
  {
    return Uni.createFrom().item(Instant.ofEpochMilli(ntpClient.getTime(inetAddress)
      .getMessage().getTransmitTimeStamp().getTime()).atZone(ZoneId.systemDefault())
      .format(DateTimeFormatter.ofPattern(FMT)));
  }

  @GET
  @Path("{zoneId}")
  public Uni<String> zonedTime(@PathParam("zoneId") String zoneId) throws IOException
  {
    return Uni.createFrom().item(Instant.ofEpochMilli(ntpClient.getTime(inetAddress)
        .getMessage().getTransmitTimeStamp().getTime())
      .atZone(ZoneId.of(URLDecoder.decode(zoneId, StandardCharsets.UTF_8)))
      .format(DateTimeFormatter.ofPattern(FMT)));
  }
}
