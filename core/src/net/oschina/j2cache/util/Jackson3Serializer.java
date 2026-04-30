/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

/**
 * 使用 jackson3 进行对象的 JSON 格式序列化
 */
public class Jackson3Serializer implements Serializer {

    private final static ObjectMapper MAPPER = JsonMapper.builder()
            .activateDefaultTyping(
                    BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build(),
                    DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY)
            .build();

    @Override
    public String name() {
        return "jackson3";
    }

    @Override
    public byte[] serialize(Object obj) {
        return MAPPER.writeValueAsBytes(obj);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return MAPPER.readValue(bytes, Object.class);
    }

}
