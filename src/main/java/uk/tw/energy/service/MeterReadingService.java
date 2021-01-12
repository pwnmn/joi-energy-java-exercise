package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MeterReadingService {

    private final Map<String, MeterReadings> meterAssociatedReadings;

    public MeterReadingService(Map<String, MeterReadings> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    public Optional<MeterReadings> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    public void storeReadings(MeterReadings meterReadings) {
        if (!meterAssociatedReadings.containsKey(meterReadings.getSmartMeterId())) {
            meterAssociatedReadings.put(meterReadings.getSmartMeterId(), meterReadings);
        }
    }
}
