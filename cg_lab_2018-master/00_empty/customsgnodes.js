
//custom model structure:
/*
let model = {
  position: position,
  normal: normal,
  texture: texture,
  index: index
};
 */

/**
 * Renders a farmhouse at the local origin
 * width is supposed to be the long side of the house, length the short side
 */
class FarmhouseSGNode extends RenderSGNode {

  constructor(w /*width*/, l /*length*/, totalHeight, children) {
    let h = totalHeight * 0.6;    //height of cuboid
    let rh = totalHeight;         //roof-height
    let rw = w * 0.8;             //roof-width

    //cuboid vertex points per row: left bottom, right bottom, right top, left top
    let vertices = new Float32Array([
        //cuboid front plane (vertices 0-3)
       -w,-h,-l,   w,-h,-l,   w, h,-l,   -w, h,-l,
       //cuboid back plane (vertices 4-7)
       -w,-h, l,   w,-h, l,   w, h, l,   -w, h, l,
       //roof (vertices 8-9)
       -rw,rh,0,   rw,rh,0
    ]);

    //an index consists of 3 vertices that make up a triangle face
    let indices =  new Float32Array([
       //cuboid indices
       0,1,2,   0,2,3,
       4,5,6,   4,6,7,
       1,2,5,   2,5,6,
       0,3,4,   3,4,7,
       // 2,3,6,   3,6,7, <-- cuboid roof not needed
       0,1,5,   0,4,5,
       //roof indices
       3,7,8,   2,6,9,
       2,3,8,   2,8,9,
       6,7,9,   7,8,9
    ]);

    //TODO: specify normals and texCoords

    let model = {
      position: vertices,
      index: indices
      //normal: normals,
      //texture: texCoords,
    }
    super(model, children);
  }
}



class LODRenderSGNode extends SGNode {

  constructor(position, model_lod0, model_lod1, model_lod2, children) {
    super(children);
    this.position = position;
    this.lod0Renderer = new RenderSGNode(model_lod0);
    this.lod1Renderer = new RenderSGNode(model_lod1);
    this.lod2Renderer = new RenderSGNode(model_lod2);
  }

  render(context) {
    let distanceToCam = getVectorLength(getVec3VectorDistance(this.position, camera.position));

    if (distanceToCam < 150)
      this.lod2Renderer.render(context);
    else if (distanceToCam < 400)
      this.lod1Renderer.render(context);
    else
      this.lod0Renderer.render(context);

    //render children
    super.render(context);
  }
}
