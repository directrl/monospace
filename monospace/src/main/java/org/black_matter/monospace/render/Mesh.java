package org.black_matter.monospace.render;

import lombok.Getter;
import org.black_matter.monospace.model.Model;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memCallocFloat;
import static org.lwjgl.system.MemoryUtil.memCallocInt;

public class Mesh implements Closeable {
	
	private final List<Integer> vboIds = new ArrayList<>();
	
	@Getter private int vertexCount;
	@Getter private int vaoId;
	
	public Mesh(float[] positions, float[] texCoords, int[] indices) {
		this.vertexCount = indices.length;
		
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		// positions
		var vboId = glGenBuffers();
		vboIds.add(vboId);
		
		FloatBuffer positionsBuffer = memCallocFloat(positions.length);
		positionsBuffer.put(0, positions);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
		
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		// texture coordinates
		vboId = glGenBuffers();
		vboIds.add(vboId);
		
		FloatBuffer texCoordsBuffer = memCallocFloat(texCoords.length);
		texCoordsBuffer.put(0, texCoords);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		
		// indices
		vboId = glGenBuffers();
		vboIds.add(vboId);
		
		IntBuffer indicesBuffer = memCallocInt(indices.length);
		indicesBuffer.put(0, indices);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	public static Mesh createForModel(AIMesh mesh, Model model) {
		// vertices
		AIVector3D.Buffer buffer = mesh.mVertices();
		float[] vertices = new float[buffer.remaining() * 3];
		int pos = 0;
		
		while(buffer.remaining() > 0) {
			var texCoord = buffer.get();
			vertices[pos++] = texCoord.x();
			vertices[pos++] = texCoord.y();
			vertices[pos++] = texCoord.z();
		}
		
		// texCoords
		buffer = mesh.mTextureCoords(0);
		float[] texCoords;
		
		if(buffer == null) {
			texCoords = new float[] { };
		} else {
			texCoords = new float[buffer.remaining() * 2];
			pos = 0;
			
			while(buffer.remaining() > 0) {
				var texCoord = buffer.get();
				texCoords[pos++] = texCoord.x();
				texCoords[pos++] = 1 - texCoord.y();
			}
		}
		
		// indices
		List<Integer> indices = new ArrayList<>();
		int numFaces = mesh.mNumFaces();
		AIFace.Buffer fBuffer = mesh.mFaces();
		
		for(int i = 0; i < numFaces; i++) {
			var face = fBuffer.get(i);
			var mBuffer = face.mIndices();
			
			while(mBuffer.remaining() > 0) {
				indices.add(mBuffer.get());
			}
		}
		
		return new Mesh(vertices, texCoords, indices.stream().mapToInt(Integer::intValue).toArray());
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
