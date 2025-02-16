package io.jenkins.plugins.aws.kinesisconsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import hudson.util.FormValidation;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.JenkinsRule;

@For(GlobalKinesisConfiguration.class)
public class GlobalKinesisConfigurationTest {

  @Rule public JenkinsRule j = new JenkinsRule();

  @Test
  public void configRoundtrip() {
    GlobalKinesisConfiguration c = GlobalKinesisConfiguration.get();
    c.setLocalEndpoint("http://localhost:4566");
    c.setRegion("eu-east-1");
    c.setKinesisConsumerEnabled(true);
    c.setKinesisStreamItems(ImmutableList.of(new KinesisStreamItem("stream_foo", "LATEST")));
    c.save();
    c.load();
  }

  @Test
  public void shouldCheckInvalidLocalEndpoint() {
    GlobalKinesisConfiguration c = GlobalKinesisConfiguration.get();

    FormValidation result = c.doCheckLocalEndpoint("not_a_url");

    assertEquals(result.kind, FormValidation.Kind.ERROR);
    assertEquals(
        result.getMessage(), FormValidation.error("'not_a_url' is not a valid URL").getMessage());
  }

  @Test
  public void shouldCheckValidLocalEndpoint() {
    GlobalKinesisConfiguration c = GlobalKinesisConfiguration.get();

    assertEquals(c.doCheckLocalEndpoint("http://localhost:4566").kind, FormValidation.Kind.OK);
  }

  @Test
  public void shouldCheckInvalidRegion() {
    GlobalKinesisConfiguration c = GlobalKinesisConfiguration.get();

    FormValidation result = c.doCheckRegion("foo-bar");

    assertEquals(result.kind, FormValidation.Kind.ERROR);
    assertTrue(result.getMessage().contains("not a valid AWS region"));
  }

  @Test
  public void shouldCheckValidRegion() {
    GlobalKinesisConfiguration c = GlobalKinesisConfiguration.get();

    FormValidation result = c.doCheckRegion("us-east-1");

    assertEquals(result.kind, FormValidation.Kind.OK);
  }

  @Test
  public void shouldCheckInvalidInitialPositionInStream() {
    GlobalKinesisConfiguration c = GlobalKinesisConfiguration.get();

    FormValidation result = c.doCheckInitialPositionInStream("foo-bar");

    assertEquals(result.kind, FormValidation.Kind.ERROR);
    assertTrue(result.getMessage().contains("not a valid initial position"));
  }

  @Test
  public void shouldCheckInitialPositionInStream() {
    GlobalKinesisConfiguration c = GlobalKinesisConfiguration.get();

    assertEquals(c.doCheckInitialPositionInStream("LATEST").kind, FormValidation.Kind.OK);
    assertEquals(c.doCheckInitialPositionInStream("lAtEsT").kind,
     FormValidation.Kind.OK);
    assertEquals(c.doCheckInitialPositionInStream("TRIM_HORIZON").kind, FormValidation.Kind.OK);
    assertEquals(c.doCheckInitialPositionInStream("tRiM_HoRiZoN").kind,
     FormValidation.Kind.OK);
  }
}
