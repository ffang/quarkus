package io.quarkus.cxf.jaxrs.runtime;

import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(value = BusFactory.class)
final class BusFactorySubstitutions {

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.FromAlias)
    static private ThreadLocal<BusHolderSubstitutions> THREAD_BUS = new ThreadLocal<>();
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    static private Map<Thread, BusHolderSubstitutions> THREAD_BUSSES = null;

    @Substitute
    private static BusHolderSubstitutions getThreadBusHolder(boolean set) {
        BusHolderSubstitutions h = THREAD_BUS.get();
        if (h == null || h.stale) {

            if (h == null || h.stale) {
                h = new BusHolderSubstitutions();

            }
            if (set) {
                THREAD_BUS.set(h);
            }
        }
        return h;
    }

    @Substitute
    public static void clearDefaultBusForAnyThread(final Bus bus) {

    }

    @Substitute
    public static void setThreadDefaultBus(Bus bus) {
        if (bus == null) {
            BusHolderSubstitutions h = THREAD_BUS.get();
            if (h == null) {

            }
            if (h != null) {
                h.bus = null;
                h.stale = true;
                THREAD_BUS.remove();
            }
        } else {
            BusHolderSubstitutions b = getThreadBusHolder(true);
            b.bus = bus;
        }
    }

    @TargetClass(value = BusFactory.class, innerClass = "BusHolder")
    final static class BusHolderSubstitutions {
        @Alias
        Bus bus;
        @Alias
        volatile boolean stale;
    }

}
