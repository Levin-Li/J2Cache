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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;

/**
 * 使用 fastjson2 进行对象的 JSON 格式序列化
 */
public class Fastjson2Serializer implements Serializer {

    @Override
    public String name() {
        return "fastjson2";
    }

    @Override
    public byte[] serialize(Object obj) {
        return JSON.toJSONBytes(obj, JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.WriteClassName, JSONWriter.Feature.FieldBased);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return JSON.parse(bytes, JSONReader.Feature.SupportAutoType, JSONReader.Feature.FieldBased);
    }

}
