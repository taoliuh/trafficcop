package com.willowtreeapps.trafficcop.test;

import com.willowtreeapps.trafficcop.DataUsage;
import com.willowtreeapps.trafficcop.DataUsageAlertAdapter;
import com.willowtreeapps.trafficcop.TrafficCop;
import com.willowtreeapps.trafficcop.Threshold;
import com.willowtreeapps.trafficcop.test.helpers.TestDataUsageStatsProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.willowtreeapps.trafficcop.SizeUnit.KILOBYTES;
import static com.willowtreeapps.trafficcop.TimeUnit.SECOND;
import static com.willowtreeapps.trafficcop.TimeUnit.SECONDS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by evantatarka on 10/8/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class TrafficCopTest {
    @Test
    public void testOnPauseUnder() {
        DataUsageAlertAdapter mockAdapter = mock(DataUsageAlertAdapter.class);
        TestDataUsageStatsProvider testProvider = new TestDataUsageStatsProvider();
        TrafficCop trafficCop = new TrafficCop.Builder()
                .downloadWarningThreashold(Threshold.of(100, KILOBYTES).per(SECOND))
                .alert(mockAdapter)
                .dataUsageStatsProvider(testProvider)
                .create(Robolectric.application);

        trafficCop.onResume();
        testProvider.incrementTime(1, SECOND);
        trafficCop.onPause();

        verify(mockAdapter, never()).alertThreshold(any(DataUsage.class));
    }

    @Test
    public void testOnPauseOver() {
        DataUsageAlertAdapter mockAdapter = mock(DataUsageAlertAdapter.class);
        TestDataUsageStatsProvider testProvider = new TestDataUsageStatsProvider();
        TrafficCop trafficCop = new TrafficCop.Builder()
                .downloadWarningThreashold(Threshold.of(100, KILOBYTES).per(SECOND))
                .alert(mockAdapter)
                .dataUsageStatsProvider(testProvider)
                .create(Robolectric.application);

        trafficCop.onResume();
        testProvider.incrementTime(1, SECOND);
        testProvider.incrementReceived(100, KILOBYTES);
        trafficCop.onPause();

        verify(mockAdapter).alertThreshold(DataUsage.download(100, KILOBYTES).in(1, SECOND));
    }

    @Test
    public void testOnPauseAndResumeUnder() {
        DataUsageAlertAdapter mockAdapter = mock(DataUsageAlertAdapter.class);
        TestDataUsageStatsProvider testProvider = new TestDataUsageStatsProvider();
        TrafficCop trafficCop = new TrafficCop.Builder()
                .downloadWarningThreashold(Threshold.of(100, KILOBYTES).per(2, SECONDS))
                .alert(mockAdapter)
                .dataUsageStatsProvider(testProvider)
                .create(Robolectric.application);

        trafficCop.onResume();
        testProvider.incrementTime(1, SECOND);
        testProvider.incrementReceived(40, KILOBYTES);
        trafficCop.onPause();
        trafficCop.onResume();
        testProvider.incrementTime(1, SECOND);
        testProvider.incrementReceived(40, KILOBYTES);
        trafficCop.onPause();

        verify(mockAdapter, never()).alertThreshold(any(DataUsage.class));
    }

    @Test
    public void testOnPauseAndOverUnder() throws InterruptedException {
        DataUsageAlertAdapter mockAdapter = mock(DataUsageAlertAdapter.class);
        TestDataUsageStatsProvider testProvider = new TestDataUsageStatsProvider();
        TrafficCop trafficCop = new TrafficCop.Builder()
                .downloadWarningThreashold(Threshold.of(100, KILOBYTES).per(2, SECONDS))
                .alert(mockAdapter)
                .dataUsageStatsProvider(testProvider)
                .create(Robolectric.application);

        trafficCop.onResume();
        testProvider.incrementTime(1, SECOND);
        testProvider.incrementReceived(50, KILOBYTES);
        trafficCop.onPause();
        trafficCop.onResume();
        testProvider.incrementTime(1, SECOND);
        testProvider.incrementReceived(50, KILOBYTES);
        trafficCop.onPause();

        verify(mockAdapter).alertThreshold(DataUsage.download(100, KILOBYTES).in(2, SECONDS));
    }

    @Test
    public void testOnPauseAndOverUnderOnce() throws InterruptedException {
        DataUsageAlertAdapter mockAdapter = mock(DataUsageAlertAdapter.class);
        TestDataUsageStatsProvider testProvider = new TestDataUsageStatsProvider();
        TrafficCop trafficCop = new TrafficCop.Builder()
                .downloadWarningThreashold(Threshold.of(100, KILOBYTES).per(1, SECONDS))
                .alert(mockAdapter)
                .dataUsageStatsProvider(testProvider)
                .create(Robolectric.application);

        trafficCop.onResume();
        testProvider.incrementTime(1, SECOND);
        testProvider.incrementReceived(100, KILOBYTES);
        trafficCop.onPause();
        trafficCop.onResume();
        testProvider.incrementTime(1, SECOND);
        trafficCop.onPause();

        verify(mockAdapter, times(1)).alertThreshold(DataUsage.download(100, KILOBYTES).in(1, SECONDS));
    }
}