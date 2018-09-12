package org.jboss.windup.rules.apps.java.reporting.rules;

import java.io.IOException;

import org.jboss.windup.reporting.model.ApplicationDependencyGraphDTO;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ApplicationDependencyGraphDTOItemSerializer extends StdSerializer<ApplicationDependencyGraphDTO> {

	private static final long serialVersionUID = -3443341738848344508L;
	
	private static final String KIND = "kind";
	private static final String METADATA = "metadata";
	private static final String NAME = "name";

	protected ApplicationDependencyGraphDTOItemSerializer(Class<ApplicationDependencyGraphDTO> t) {
		super(t);
	}

	public ApplicationDependencyGraphDTOItemSerializer() {
		this(null);
	}

	@Override
	public void serialize(ApplicationDependencyGraphDTO dto, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(dto.getName());
		gen.writeRaw(":");
		gen.writeStartObject();
		gen.writeStringField(KIND, dto.getType());
		gen.writeObjectFieldStart(METADATA);
		gen.writeObjectField(NAME, dto.getName());
		gen.writeEndObject();
	}
	
	public static void main(String[] args) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addSerializer(ApplicationDependencyGraphDTO.class, new ApplicationDependencyGraphDTOItemSerializer());
		mapper.registerModule(module);
		
		System.out.println(mapper.writeValueAsString(new ApplicationDependencyGraphDTO()));
	}

}
