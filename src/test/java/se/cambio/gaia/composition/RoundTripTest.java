package se.cambio.gaia.composition;

import com.nedap.archie.rm.composition.Composition;
import org.apache.commons.io.IOUtils;
import org.ehrbase.client.flattener.Flattener;
import org.ehrbase.client.flattener.Unflattener;
import org.ehrbase.serialisation.jsonencoding.CanonicalJson;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.cambio.gaia.composition.analytereport8composition.AnalyteReport8Composition;
import se.cambio.gaia.composition.support.TestTemplateProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RoundTripTest.TestConfig.class)
public class RoundTripTest {

    @Value("composition/analyte.json")
    Resource analyteCompositionResource;

    @Test
    public void shouldAllowRoundTrip() throws IOException {
        TestTemplateProvider templateProvider = new TestTemplateProvider();
        CanonicalJson cut = new CanonicalJson();

        String originalComposition = IOUtils.toString(
                analyteCompositionResource.getInputStream(), StandardCharsets.UTF_8);

        // Deserialization
        Flattener flattener = new Flattener(templateProvider);
        Composition composition = cut.unmarshal(originalComposition);
        AnalyteReport8Composition analyteComposition =
                flattener.flatten(composition, AnalyteReport8Composition.class);

        // Serialization
        Unflattener unflattener = new Unflattener(templateProvider);
        composition = (Composition) unflattener.unflatten(analyteComposition);
        String serializedComposition = cut.marshal(composition);

        assertThat(serializedComposition, equalTo(originalComposition));
    }

    @Configuration
    static class TestConfig {

    }
}
