/***************************************************************
* file: Cube.java
* author: Colin Trotter
* class: CS 445 – Computer Graphics
*
* assignment: final program
* date last modified: 5/17/2017
*
* purpose: This class represents a cube in 3D space. It has a position.
*
****************************************************************/ 
package cs445.final_project;

public class Cube {
    private boolean isActive;
    private Vector3f position;
    private BlockType type;
    private float x,y,z;
    
    // enum: BlockType
    // purpose: enumeration to hold the type of block that should be rendered based on a blockID.
    public enum BlockType{
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5),
        BlockType_Default(6);
        
        private int blockID;
        
        BlockType(int i) {
            blockID = i;
        }
        public int getID(){
            return blockID;
        }
        public void setID(int i){
            blockID = i;
        }
    }
    
    // method: Cube
    // purpose: constructor that instantiates this Cube with the given BlockType
    public Cube(BlockType type){
        this.type = type;
    }

    // method: getPosition
    // purpose: returns the position of this Cube in the 3D display
    public Vector3f getPosition() {
        return position;
    }

    // method: getPosition
    // purpose: sets the position of this Cube in the 3D display
    public void setPosition(Vector3f position) {
        this.position = position;
    }
    
    // method: isActive
    // purpose: returns a boolean representing if the cube should be rendered or not
    public boolean isActive() {
        return isActive;
    }

    
    // method: setActive
    // purpose: sets the cube to be active (rendered) or inactive (not rendered)
    public void setActive(boolean active){
        isActive = active;
    }
    
    // method: getBlockType
    // purpose: returns the BlockType of this Cube.
    public BlockType getBlockType(){
        return type;
    }

}
