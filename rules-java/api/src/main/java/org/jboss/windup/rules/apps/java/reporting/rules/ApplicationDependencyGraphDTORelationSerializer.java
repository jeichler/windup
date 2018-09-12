package org.jboss.windup.rules.apps.java.reporting.rules;

import java.io.IOException;

import org.jboss.windup.reporting.model.ApplicationDependencyGraphDTO;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ApplicationDependencyGraphDTORelationSerializer extends StdSerializer<ApplicationDependencyGraphDTO> {

	private static final long serialVersionUID = -3443341738848344508L;
	
	private static final String SOURCE = "source";
	private static final String TARGET = "target";

	protected ApplicationDependencyGraphDTORelationSerializer(Class<ApplicationDependencyGraphDTO> t) {
		super(t);
	}

	public ApplicationDependencyGraphDTORelationSerializer() {
		this(null);
	}

	@Override
	public void serialize(ApplicationDependencyGraphDTO dto, JsonGenerator gen, SerializerProvider provider) {
		dto.getParents().forEach( item -> {
			try {
				gen.writeStartObject();
				gen.writeStringField(SOURCE, dto.getName());
				gen.writeStringField(TARGET, item);
				gen.writeEndObject();
				gen.writeRaw("," + System.lineSeparator());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
	}
	
	public static void main(String[] args) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addSerializer(ApplicationDependencyGraphDTO.class, new ApplicationDependencyGraphDTORelationSerializer());
		mapper.registerModule(module);
		
		ApplicationDependencyGraphDTO dto = new ApplicationDependencyGraphDTO();
		dto.addParent("brownie.ear");
		dto.addParent("kekse.ear");
		System.out.println(mapper.writeValueAsString(dto));
	}

}
