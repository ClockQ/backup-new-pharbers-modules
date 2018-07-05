package com.pharbers.panel.format.input.reader.nhwa;

import com.pharbers.excel.format.input.writable.common.phXlsxCommonWritable;
import org.apache.hadoop.io.NullWritable;
import com.pharbers.excel.format.input.writable.phExcelWritable;
import com.pharbers.excel.format.input.reader.common.PhExcelXLSXCommonReader;

import java.io.IOException;

public class phNhwaCpaSecondSheetReader extends PhExcelXLSXCommonReader<NullWritable, phExcelWritable> {

    @Override
    public NullWritable getCurrentKey() throws IOException, InterruptedException {
        return NullWritable.get();
    }

    @Override
    public phExcelWritable getCurrentValue() throws IOException, InterruptedException {
        phXlsxCommonWritable reVal = new phXlsxCommonWritable();
        String s = reVal.richWithInputRow(parser.currentIndex(), value);
        reVal.setValues(s);
        return reVal;
    }
}