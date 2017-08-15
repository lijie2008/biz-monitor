package com.huntkey.rx.sceo.monitor.commom.utils;

/**
 * Created by xuyf on 2017/7/3 0003.
 */
public class SequenceNumberFormatUtil {

    public static String seqNum(int seqNum, int length){
        return formatSeqNum(seqNum, length);
    }

    public static String prefixSeqNum(String prefix, int seqNum, int length){
        String newSeqNum = formatSeqNum(seqNum, length);
        newSeqNum = prefix + newSeqNum;
        return newSeqNum;
    }

    public static String prefixSeqNum(String prefix, String linkStr, int seqNum, int length){
        String newSeqNum = formatSeqNum(seqNum, length);
        newSeqNum = prefix + linkStr + newSeqNum;
        return newSeqNum;
    }

    private static String formatSeqNum(int sourceNum, int formatLength){
        String newSeqNum = String.format("%0"+formatLength+"d", sourceNum);
        return newSeqNum;
    }
}
