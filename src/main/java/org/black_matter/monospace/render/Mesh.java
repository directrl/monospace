package org.black_matter.monospace.render;

import lombok.Getter;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;

public class Mesh implements Closeable {
	
	private final List<Integer> vboIds = new ArrayList<>();
	
	@Getter private int vertexCount;
	@Getter private int vaoId;
	
	public Mesh(float[] positions, float[] texCoords, int[] indices) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			this.vertexCount = indices.length;
			
			vaoId = glGenVertexArrays();
			glBindVertexArray(vaoId);
			
			// positions
			var vboId = glGenBuffers();
			vboIds.add(vboId);
			
			FloatBuffer positionsBuffer = stack.callocFloat(positions.length);
			positionsBuffer.put(0, positions);
			
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
			
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			
			// texture coordinates
			vboId = glGenBuffers();
			vboIds.add(vboId);
			
			FloatBuffer texCoordsBuffer = stack.callocFloat(texCoords.length);
			texCoordsBuffer.put(0, texCoords);
			
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(1);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
			
			// indices
			vboId = glGenBuffers();
			vboIds.add(vboId);
			
			IntBuffer indicesBuffer = stack.callocInt(indices.length);
			indicesBuffer.put(0, indices);
			
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
			
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		}
	}
	
	public void render() {
		glBindVertexArray(vaoId);
		glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
	}
	
	@Override
	public void close() {
		vboIds.forEach(GL30::glDeleteBuffers);
		glDeleteVertexArrays(vaoId);
	}
}
