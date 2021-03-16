/*
 *
 *   Copyright [ 2020 - 2021 ] [Matthew Buckton]
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.maps.selector.operators.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maps.selector.Identifier;
import org.maps.selector.Message;
import org.maps.selector.MessageBuilder;
import org.maps.selector.ParseException;
import org.maps.selector.operators.FunctionOperator;
import org.maps.selector.operators.IdentifierResolver;
import org.maps.selector.operators.extentions.ParserFactory;

public class ParserTest {

  @Test
  public void parserLoadToStringTest() throws ParseException {
    String[] arguments = {"value"};
    FunctionOperator operation = ParserFactory.getInstance().loadParser("json", Arrays.asList(arguments));
    FunctionOperator operation2 = ParserFactory.getInstance().loadParser("json", Arrays.asList(arguments));
    Assertions.assertEquals("Parse (JSON, 'value' ,)", operation.toString());
    Assertions.assertEquals(operation,operation2);
    Assertions.assertEquals(operation.hashCode(),operation2.hashCode());
    Assertions.assertNotEquals(operation, this);
  }

  @Test
  public void exceptions() throws ParseException {
    String[] empty = {};
    Assertions.assertThrows(ParseException.class, ()-> ParserFactory.getInstance().loadParser("json", Arrays.asList(empty)));
    String[]  arguments = {"secondLevel.data"};
    FunctionOperator operation = ParserFactory.getInstance().loadParser("json", Arrays.asList(arguments));

    String jsonString = "";
    MessageBuilder messageBuilder = new MessageBuilder();
    messageBuilder.setOpaqueData(jsonString.getBytes());
    Assertions.assertNotEquals(100L, operation.evaluate(messageBuilder.build()));
  }

  @Test
  public void jsonWalking() throws ParseException {
    String jsonString = "{\"test\": 10, \"value\": 20, \"secondLevel\": { \"test\": 30, \"data\": 40 },\"array\": [ 10, 20],\"arrayData\": [{ \"fred\": 50}, {\"bill\": 60 }]}";
    String[]  arguments = {"secondLevel.data"};
    FunctionOperator operation = ParserFactory.getInstance().loadParser("json", Arrays.asList(arguments));
    MessageBuilder messageBuilder = new MessageBuilder();
    messageBuilder.setOpaqueData(jsonString.getBytes());
    IdentifierResolver message = messageBuilder.build();
    Assertions.assertEquals(40L, operation.evaluate(message));

    String[] arrayArgs = {"array.1"};
    operation = ParserFactory.getInstance().loadParser("json", Arrays.asList(arrayArgs));
    Assertions.assertEquals(20L, operation.evaluate(message));

    String[] arrayDeepArgs = {"arrayData.1.bill"};
    operation = ParserFactory.getInstance().loadParser("json", Arrays.asList(arrayDeepArgs));
    Assertions.assertEquals(60L, operation.evaluate(message));

    jsonString = "{\"array\": [ [ 10, 20, 30],[ 40, 50, 60]]}";
    String[] multiArrays = {"array.1.2"};
    operation = ParserFactory.getInstance().loadParser("json", Arrays.asList(multiArrays));
    messageBuilder = new MessageBuilder();
    messageBuilder.setOpaqueData(jsonString.getBytes());
    message = messageBuilder.build();
    Assertions.assertEquals(60L, operation.evaluate(message));

  }

  @Test
  public void parserLoadTest() throws ParseException {
    String[] arguments = {"value"};
    FunctionOperator operation = ParserFactory.getInstance().loadParser("json", Arrays.asList(arguments));
    String jsonString = "{test:10; value:20}";
    MessageBuilder messageBuilder = new MessageBuilder();
    messageBuilder.setOpaqueData(jsonString.getBytes());
    Assertions.assertEquals(20L, operation.evaluate(messageBuilder.build()));
    Assertions.assertNotEquals(21L, operation.evaluate(messageBuilder.build()));
  }

  @Test
  public void parserLoadDoubleTest() throws ParseException {
    String[] arguments = {"value"};
    FunctionOperator operation = ParserFactory.getInstance().loadParser("json", Arrays.asList(arguments));
    String jsonString = "{test:10.0; value:20.0}";
    MessageBuilder messageBuilder = new MessageBuilder();
    messageBuilder.setOpaqueData(jsonString.getBytes());
    Assertions.assertEquals(20.0, operation.evaluate(messageBuilder.build()));
    Assertions.assertNotEquals(201.0, operation.evaluate(messageBuilder.build()));
  }

  @Test
  public void parserLoadStringTest() throws ParseException {
    String[] arguments = {"value"};
    FunctionOperator operation = ParserFactory.getInstance().loadParser("json", Arrays.asList(arguments));
    String jsonString = "{test:10.0; value:'hello'}";
    MessageBuilder messageBuilder = new MessageBuilder();
    messageBuilder.setOpaqueData(jsonString.getBytes());
    Assertions.assertEquals("hello", operation.evaluate(messageBuilder.build()));
    Assertions.assertNotEquals("hello1", operation.evaluate(messageBuilder.build()));
  }


  @Test
  public void parserPathLoadTest() throws ParseException {
    String[] arguments = {"second.value"};
    FunctionOperator operation = ParserFactory.getInstance().loadParser("json", Arrays.asList(arguments));
    String jsonString = "{test:10; value:20; second:{value:430; test:20 } }";
    MessageBuilder messageBuilder = new MessageBuilder();
    messageBuilder.setOpaqueData(jsonString.getBytes());
    Assertions.assertEquals(430l, operation.evaluate(messageBuilder.build()));
  }

  @Test
  public void parseFromIdentifier() throws ParseException{
    String[] arguments = {"value"};
    FunctionOperator operation = ParserFactory.getInstance().loadParser(new Identifier("protocol"), Arrays.asList(arguments));
    String jsonString = "{test:10; value:20}";
    MessageBuilder messageBuilder = new MessageBuilder();
    Map<String, Object> dataMap = new HashMap<>();
    dataMap.put("protocol", "json");
    messageBuilder.setDataMap(dataMap);
    messageBuilder.setOpaqueData(jsonString.getBytes());
    Assertions.assertEquals(20l, operation.evaluate(messageBuilder.build()));
  }

  @Test
  public void unknownParseFromIdentifier() throws ParseException{
    String[] arguments = {"value"};
    FunctionOperator operation = ParserFactory.getInstance().loadParser(new Identifier("protocol"), Arrays.asList(arguments));
    String jsonString = "{test:10; value:20}";
    MessageBuilder messageBuilder = new MessageBuilder();
    Map<String, Object> dataMap = new HashMap<>();
    dataMap.put("protocol", "secret");
    messageBuilder.setDataMap(dataMap);
    messageBuilder.setOpaqueData(jsonString.getBytes());
    Assertions.assertEquals(false, operation.evaluate(messageBuilder.build()));
  }

}
