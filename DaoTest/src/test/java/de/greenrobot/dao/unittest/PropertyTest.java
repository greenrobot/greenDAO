package de.greenrobot.dao.unittest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import de.greenrobot.dao.Property;
import de.greenrobot.daotest.dummyapp.BuildConfig;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 16)
public class PropertyTest {

    @Test
    public void testEquality() {
        final Property a1Property = new Property(0, Long.class, "a", true, "_a");
        final Property a2Property = new Property(0, Long.class, "a", true, "_a");

        final Property bProperty = new Property(1, Long.class, "a", true, "_a");
        final Property cProperty = new Property(0, Integer.class, "a", true, "_a");
        final Property dProperty = new Property(0, Long.class, "b", true, "_a");
        final Property eProperty = new Property(0, Long.class, "a", false, "_a");
        final Property fProperty = new Property(0, Long.class, "a", true, "_b");

        assertEquals(a1Property, a2Property);
        assertNotSame(a1Property, a2Property);

        assertFalse(a1Property.equals(bProperty));
        assertFalse(a1Property.equals(cProperty));
        assertFalse(a1Property.equals(dProperty));
        assertFalse(a1Property.equals(eProperty));
        assertFalse(a1Property.equals(fProperty));
    }


}
