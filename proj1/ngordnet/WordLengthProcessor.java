package ngordnet;

public class WordLengthProcessor implements YearlyRecordProcessor {
    public double process(YearlyRecord yearlyRecord) {
        long total = 0, numerator = 0;
        for (String s : yearlyRecord.words()) {
            int count = yearlyRecord.count(s);
            numerator += s.length() * count;
            total += count;
        }
        return ((double) numerator / total);
    }
}
