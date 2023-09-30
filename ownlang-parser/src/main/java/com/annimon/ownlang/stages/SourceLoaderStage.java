package com.annimon.ownlang.stages;

import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.parser.SourceLoader;
import java.io.IOException;

public class SourceLoaderStage implements Stage<String, String> {

    public static final String TAG_SOURCE = "source";

    @Override
    public String perform(StagesData stagesData, String input) {
        try {
            String result = SourceLoader.readSource(input);
            stagesData.put(TAG_SOURCE, result);
            return result;
        } catch (IOException e) {
            throw new OwnLangRuntimeException("Unable to read input " + input, e);
        }
    }
}
