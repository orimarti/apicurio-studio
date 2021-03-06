/*
 * Copyright 2017 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apicurio.hub.core.beans;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

/**
 * Represents some basic information extracted from an API Design resource (the 
 * content of an API design, typically an OpenAPI document).
 * @author eric.wittmann@gmail.com
 */
public class ApiDesignResourceInfo {

    protected static final ObjectMapper jsonMapper;
    protected static final ObjectMapper yamlMapper;
    static {
        jsonMapper = new ObjectMapper();
        jsonMapper.setSerializationInclusion(Include.NON_NULL);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        YAMLFactory factory = new YAMLFactory();
        factory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        yamlMapper = new ObjectMapper(factory);
        yamlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ApiDesignResourceInfo fromContent(String content) throws Exception {
        String name = null;
        String description = null;
        FormatType format = FormatType.JSON;
        ObjectMapper mapper = jsonMapper;
        
        if (!content.startsWith("{")) {
            format = FormatType.YAML;
            mapper = yamlMapper;
        }
        
        OpenApi3Document document = mapper.readerFor(OpenApi3Document.class).readValue(content);
        if (document.getInfo() != null) {
            if (document.getInfo().getTitle() != null) {
                name = document.getInfo().getTitle();
            }
            if (document.getInfo().getDescription() != null) {
                description = document.getInfo().getDescription();
            }
        }
        
        ApiDesignResourceInfo info = new ApiDesignResourceInfo();
        info.setFormat(format);
        info.setName(name);
        info.setDescription(description);
        if (document.getTags() != null) {
            for (Tag tag : document.getTags()) {
                info.getTags().add(tag.getName());
            }
        }
        return info;
    }
    
    
    private String name;
    private String description;
    private Set<String> tags = new HashSet<>();
    private FormatType format;
    
    /**
     * Constructor.
     */
    public ApiDesignResourceInfo() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the tags
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    /**
     * @return the format
     */
    public FormatType getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(FormatType format) {
        this.format = format;
    }

}
