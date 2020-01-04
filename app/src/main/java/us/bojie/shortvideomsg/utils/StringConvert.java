package us.bojie.shortvideomsg.utils;

public class StringConvert {
    public static String convertFeedUgc(int count) {
        if (count < 1000) {
            return String.valueOf(count);
        }
        return count / 1000 + "k";
    }
}
