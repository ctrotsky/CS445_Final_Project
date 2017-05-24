/***************************************************************
* file: Chunk.java
* author: Colin Trotter
* author: Cristian-Garcia
* author: Rocky Qiu
* author: Mirza Hasan Baig
* class: CS 445 – Computer Graphics
*
* assignment: final program
* date last modified: 5/24/2017
*
* purpose: This class represents a cube in 3D space. It has a position.
*
****************************************************************/ 
package cs445.final_project;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Cube[][][] Cubes;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private int VBOTextureHandle;
    private Texture texture;
    
    // method: render
    // purpose: pushes a matrix to be rendered that contains all of the Cubes within this chunk, then draws the arrays.
    public void render(){
        glPushMatrix();
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    // method: rebuildMesh
    // purpose: adds data from all cubes within this chunk to buffers to be rendered.
    public void rebuildMesh(float startX, float startY, float startZ) {
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        int height=0;
        int pheight=0;
        SimplexNoise noise;
        Random r= new Random();
        float p=0;
        int floor =0;
        while (p<.03)
        {
            p=r.nextFloat();
        }
        int seed= 25*r.nextInt();
        noise=new SimplexNoise(CHUNK_SIZE,p,seed);
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE)* 6 * 12);
        for (int x = 0; x < CHUNK_SIZE; x += 1) {
            for (int z = 0; z < CHUNK_SIZE; z += 1) {
                int i= (int)(StartX+x*((300-startX)/640));
                int k= (int)(StartZ+z*((300-startZ)/640));
               
                if(floor%10 ==0 && x%3==0)
                {
                height =(StartY+(int)(100*noise.getNoise(i,k)*CHUNK_SIZE));
                height= height%10 +20;
                if((pheight-height) > 3 )
                    height = height +Math.abs(height-pheight);
                }
                for(int y = 0; y < height; y++){
                    pickBlockType(x,y,z,height);    //pick which type the block should be based on height
                    VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), (float)(y * CUBE_LENGTH + (int)(CHUNK_SIZE*.8)), (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Cubes[(int) x][(int) y][(int) z])));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, Cubes[(int)(x)][(int) (y)][(int) (z)]));
                    
                }
                floor++;
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    // method: createCubeVertexCol
    // purpose: creates an array representing the colors of each cube within this chunk
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    
    // method: createCube
    // purpose: returns a float array representing all faces of a new cube at the given position
    public static float[] createCube(float x, float y, float z) {
    int offset = CUBE_LENGTH / 2;
    return new float[] {
        // TOP QUAD
        x + offset, y + offset, z,
        x - offset, y + offset, z,
        x - offset, y + offset, z - CUBE_LENGTH,
        x + offset, y + offset, z - CUBE_LENGTH,
        // BOTTOM QUAD
        x + offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z,
        x + offset, y - offset, z,
        // FRONT QUAD
        x + offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        // BACK QUAD
        x + offset, y - offset, z,
        x - offset, y - offset, z,
        x - offset, y + offset, z,
        x + offset, y + offset, z,
        // LEFT QUAD
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z,
        x - offset, y - offset, z,
        x - offset, y - offset, z - CUBE_LENGTH,
        // RIGHT QUAD
        x + offset, y + offset, z,
        x + offset, y + offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z };
    }
    
    // method: createTexCube
    // purpose: returns a float array representing all textured faces of a new cube at the given position
    public static float[] createTexCube(float x, float y, Cube cube) {
        float offset = (1024f/16)/1024f;
        
        float[] topFace, bottomFace, frontFace, backFace, leftFace, rightFace;
        
        switch (cube.getBlockType().getID()) {
            case 0:
                //Grass
                topFace = getTextureFace(x,y,offset,2,9,false);
                bottomFace = getTextureFace(x,y,offset,2,0,false);
                frontFace = getTextureFace(x,y,offset,3,0,false);
                backFace = getTextureFace(x,y,offset,3,0,true);
                leftFace = getTextureFace(x,y,offset,3,0,false);
                rightFace = getTextureFace(x,y,offset,3,0,false);
                break;
            case 1:
                //Sand
                topFace = getTextureFace(x,y,offset,2,1,false);
                bottomFace = getTextureFace(x,y,offset,2,1,false);
                frontFace = getTextureFace(x,y,offset,2,1,false);
                backFace = getTextureFace(x,y,offset,2,1,false);
                leftFace = getTextureFace(x,y,offset,2,1,false);
                rightFace = getTextureFace(x,y,offset,2,1,false);
                break;
            case 2:
                //Water
                topFace = getTextureFace(x,y,offset,14,0,false);
                bottomFace = getTextureFace(x,y,offset,14,0,false);
                frontFace = getTextureFace(x,y,offset,14,0,false);
                backFace = getTextureFace(x,y,offset,14,0,false);
                leftFace = getTextureFace(x,y,offset,14,0,false);
                rightFace = getTextureFace(x,y,offset,14,0,false);
                break;
            case 3:
                //Dirt
                topFace = getTextureFace(x,y,offset,2,0,false);
                bottomFace = getTextureFace(x,y,offset,2,0,false);
                frontFace = getTextureFace(x,y,offset,2,0,false);
                backFace = getTextureFace(x,y,offset,2,0,false);
                leftFace = getTextureFace(x,y,offset,2,0,false);
                rightFace = getTextureFace(x,y,offset,2,0,false);
                break;
            case 4:
                //Stone
                topFace = getTextureFace(x,y,offset,1,0,false);
                bottomFace = getTextureFace(x,y,offset,1,0,false);
                frontFace = getTextureFace(x,y,offset,1,0,false);
                backFace = getTextureFace(x,y,offset,1,0,false);
                leftFace = getTextureFace(x,y,offset,1,0,false);
                rightFace = getTextureFace(x,y,offset,1,0,false);
                break;
            case 5:
                //Bedrock
                topFace = getTextureFace(x,y,offset,1,1,false);
                bottomFace = getTextureFace(x,y,offset,1,1,false);
                frontFace = getTextureFace(x,y,offset,1,1,false);
                backFace = getTextureFace(x,y,offset,1,1,false);
                leftFace = getTextureFace(x,y,offset,1,1,false);
                rightFace = getTextureFace(x,y,offset,1,1,false);
                break;
            default:
                topFace = getTextureFace(x,y,offset,2,9,false);
                bottomFace = getTextureFace(x,y,offset,2,0,false);
                frontFace = getTextureFace(x,y,offset,3,0,false);
                backFace = getTextureFace(x,y,offset,3,0,false);
                leftFace = getTextureFace(x,y,offset,3,0,false);
                rightFace = getTextureFace(x,y,offset,3,0,false);
        }
        return mergeFaceArrays(topFace, bottomFace, frontFace, backFace, leftFace, rightFace);
        
    }
    
    // method: getTextureFace
    // purpose: returns a float array representing a single textured face of a cube.
    // The texture will be taken from the texture file at the coordinates given by textureX and textureY
    private static float[] getTextureFace(float x, float y, float offset, int textureX, int textureY, boolean flipVertical){
        if (flipVertical){
            return new float[] {
                    // TOP TEXTURE
                    x + offset*(textureX+1), y + offset*(textureY+1),
                    x + offset*(textureX), y + offset*(textureY+1),
                    x + offset*(textureX), y + offset*(textureY),
                    x + offset*(textureX+1), y + offset*(textureY),
            };
        }
        else{
            return new float[] {
                    // TOP TEXTURE
                    x + offset*(textureX), y + offset*(textureY),
                    x + offset*(textureX+1), y + offset*(textureY),
                    x + offset*(textureX+1), y + offset*(textureY+1),
                    x + offset*(textureX), y + offset*(textureY+1),
            };
        }
        
    }
    
    // method: mergeFaceArrays
    // purpose: merges the given arrays of textured faces into one array representing an entire textured cube
    private static float[] mergeFaceArrays(float[] topFace, float[] bottomFace, float[] frontFace, float[] backFace, float[] leftFace, float[] rightFace){
        return new float[] {
            // TOP TEXTURE
            topFace[0], topFace[1],
            topFace[2], topFace[3],
            topFace[4], topFace[5],
            topFace[6], topFace[7],
            // BOTTOM TEXTURE
            bottomFace[0], bottomFace[1],
            bottomFace[2], bottomFace[3],
            bottomFace[4], bottomFace[5],
            bottomFace[6], bottomFace[7],
            // FRONT TEXTURE
            frontFace[0], frontFace[1],
            frontFace[2], frontFace[3],
            frontFace[4], frontFace[5],
            frontFace[6], frontFace[7],
            // BACK TEXTURE
            backFace[0], backFace[1],
            backFace[2], backFace[3],
            backFace[4], backFace[5],
            backFace[6], backFace[7],
            // LEFT TEXTURE
            leftFace[0], leftFace[1],
            leftFace[2], leftFace[3],
            leftFace[4], leftFace[5],
            leftFace[6], leftFace[7],
            // RIGHT TEXTURE
            rightFace[0], rightFace[1],
            rightFace[2], rightFace[3],
            rightFace[4], rightFace[5],
            rightFace[6], rightFace[7],
        };
    }
    
    // method: getCubeColor
    // purpose: returns the color of a given cube as a float array representing the RBG values based on the BlockType
    private float[] getCubeColor(Cube cube) {
        return new float[] { 1, 1, 1 };
    }
    
    // method: Chunk
    // purpose: constructor that creates all cubes within this chunk with a random BlockType, then builds the mesh for this chunk
    public Chunk(int startX, int startY, int startZ) {
        try{
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch(Exception e)
        {
            System.out.print("ER-ROAR!");
        }
        r = new Random();
        Cubes = new Cube[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
    
    // method: pickBlockType
    // purpose: sets the cube at the given x,y,z location to a block type decided by it's height
    private void pickBlockType(int x, int y, int z, int height){
        if (y == 0){
            //bottom layer should be all bedrock
            Cubes[x][y][z] = new Cube(Cube.BlockType.BlockType_Bedrock); 
        }
        else if (y > 0 && y < height - 1){
            //middle layers should be stone or dirt
            float blockType = r.nextFloat();
            if (blockType > 0.5){
                Cubes[x][y][z] = new Cube(Cube.BlockType.BlockType_Stone); 
            }
            else{
                Cubes[x][y][z] = new Cube(Cube.BlockType.BlockType_Dirt); 
            }
        }
        else{
            //top layer should be grass, water, or sand
            float blockType = r.nextFloat();
            if (blockType > 0.1){
                Cubes[x][y][z] = new Cube(Cube.BlockType.BlockType_Grass); 
            }
            else if (blockType > 0.05){
                Cubes[x][y][z] = new Cube(Cube.BlockType.BlockType_Sand); 
            }
            else{
                Cubes[x][y][z] = new Cube(Cube.BlockType.BlockType_Water); 
            }
            
        }
    }
    
}
