package com.improving.vancouver.annotations.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.improving.vancouver.annotations.annotations.Argument;
import com.improving.vancouver.annotations.annotations.Arguments;
import com.improving.vancouver.annotations.annotations.DeleteMethod;
import com.improving.vancouver.annotations.annotations.GetMethod;
import com.improving.vancouver.annotations.annotations.PostMethod;
import com.improving.vancouver.annotations.annotations.PutMethod;
import com.improving.vancouver.annotations.annotations.RestEndpoint;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedAnnotationTypes({
    "com.improving.vancouver.annotations.annotations.RestEndpoint"
})
public class AnnotationProcessor extends AbstractProcessor {
  @Override
  public boolean process(final Set<? extends TypeElement> annotatedTypes, final RoundEnvironment roundEnv) {
    processingEnv.getMessager().printWarning("Checking annotations");

    ObjectMapper mapper = new ObjectMapper();

    for (Element element : roundEnv.getElementsAnnotatedWith(RestEndpoint.class)) {
      TypeElement restEndpointElement = (TypeElement) element;
      String name = restEndpointElement.getSimpleName().toString();
      processingEnv.getMessager().printWarning(">>> Analyzing class: " + name);

      ObjectNode root = mapper.createObjectNode();

      RestEndpoint restEndpoint = restEndpointElement.getAnnotation(RestEndpoint.class);
      root.put("path", restEndpoint.value());
      ArrayNode actionsNode = root.putArray("methods");

      for (Element childElement : restEndpointElement.getEnclosedElements()) {
        if (childElement instanceof ExecutableElement method) {
          GetMethod getMethod = method.getAnnotation(GetMethod.class);
          if (getMethod != null) {
            ObjectNode restAction = actionsNode.addObject();
            restAction.put("method", "GET");
            Arguments arguments = method.getAnnotation(Arguments.class);

            if (arguments != null) {
              ArrayNode argumentsNode = restAction.putArray("arguments");
              for (Argument argument : arguments.value()) {
                ObjectNode argumentNode = argumentsNode.addObject();
                argumentNode.put("name", argument.name());
                argumentNode.put("description", argument.description());
              }
            } else {
              Argument argument = method.getAnnotation(Argument.class);
              if (argument != null) {
                ArrayNode argumentsNode = restAction.putArray("arguments");
                ObjectNode argumentNode = argumentsNode.addObject();
                argumentNode.put("name", argument.name());
                argumentNode.put("description", argument.description());
              }
            }

            continue;
          }

          PostMethod postMethod = method.getAnnotation(PostMethod.class);
          if (postMethod != null) {
            ObjectNode restAction = actionsNode.addObject();
            restAction.put("method", "POST");

            Pattern pattern = Pattern.compile(".*\\((.*\\.class)\\)");
            Matcher matcher = pattern.matcher(postMethod.toString());
            if (matcher.matches()) {
              restAction.put("bodyType", matcher.group(1));
            }
            continue;
          }

          PutMethod putMethod = method.getAnnotation(PutMethod.class);
          if (putMethod != null) {
            ObjectNode restAction = actionsNode.addObject();
            restAction.put("method", "PUT");

            Pattern pattern = Pattern.compile(".*\\((.*\\.class)\\)");
            Matcher matcher = pattern.matcher(putMethod.toString());
            if (matcher.matches()) {
              restAction.put("bodyType", matcher.group(1));
            }
            continue;
          }

          DeleteMethod deleteMethod = method.getAnnotation(DeleteMethod.class);
          if (deleteMethod != null) {
            ObjectNode restAction = actionsNode.addObject();
            restAction.put("method", "DELETE");
            Arguments arguments = method.getAnnotation(Arguments.class);

            if (arguments != null) {
              ArrayNode argumentsNode = restAction.putArray("arguments");
              for (Argument argument : arguments.value()) {
                ObjectNode argumentNode = argumentsNode.addObject();
                argumentNode.put("name", argument.name());
                argumentNode.put("description", argument.description());
              }
            } else {
              Argument argument = method.getAnnotation(Argument.class);
              if (argument != null) {
                ArrayNode argumentsNode = restAction.putArray("arguments");
                ObjectNode argumentNode = argumentsNode.addObject();
                argumentNode.put("name", argument.name());
                argumentNode.put("description", argument.description());
                }
            }
          }
        }
      }

      try {
        FileObject fileObject = processingEnv.getFiler().createResource(
            StandardLocation.CLASS_OUTPUT,
            "",
            name + ".json",
            restEndpointElement);
        Writer writer = fileObject.openWriter();
        mapper.writerWithDefaultPrettyPrinter().writeValue(writer, root);
        writer.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return true;
  }
}
