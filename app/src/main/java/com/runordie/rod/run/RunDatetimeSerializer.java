package com.runordie.rod.run;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wsouza on 7/29/16.
 */
public class RunDatetimeSerializer extends JsonSerializer<Date> {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    //                2016-07-19T22:29:49Z

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {

        gen.writeString(df.format(value));
    }

}
