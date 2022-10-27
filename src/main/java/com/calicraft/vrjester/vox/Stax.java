package com.calicraft.vrjester.vox;

// Star Virtual Vox using stacked Voxes
public class Stax {
    public Vox alignedVox, rotatedVox;
    public Stax(Vox alignedVox, Vox rotatedVox) {
        this.alignedVox = alignedVox;
        this.rotatedVox = rotatedVox;
    }
}
