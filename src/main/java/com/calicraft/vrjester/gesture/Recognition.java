package com.calicraft.vrjester.gesture;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.List;

import static com.calicraft.vrjester.utils.tools.SpawnParticles.moveParticles;

public class Recognition {
    // Class that handles identifying a gesture utilizing the RadixTree
    // TODO - Either Check constantly, everytime Path gets appended or check at end of gesture listening.
    //      - Note, I must determine how to know when to start & stop listening to a gesture.
    // TODO - There will be 2 modes of triggering & 3 modes of terminating the recognition listener
    //      - listenOnKey | listenOnPosition
    //      - recognizeOnTime | recognizeOnRecognize | recognizeOnRelease
    //      - Upon terminating the listener, a GestureRecognition Event
    //      will either be fired. As a traced gesture makes its way through
    //      the radix sort tree, each "unlocked node" will be fired to
    //      InterMod Event Bus to notify consumers of the API that a "step"
    //      in a gesture's path has been fulfilled. This allows a way for
    //      users/devs to know if and when their gestures are being recognized

    private static int rcParticle, lcParticle;
    private static final SimpleParticleType[] particleTypes = new SimpleParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.CLOUD, ParticleTypes.BUBBLE_POP,
            ParticleTypes.FALLING_WATER};

    public Gestures gestures;

    public Recognition(Gestures gestures) {
        rcParticle = -1; lcParticle = -1;
        this.gestures = gestures;
    }

    public String recognize(Gesture gesture) { // Recognize the gesture by searching for matches in RadixTrees
        //  See the idle state for a VRDevice where we don't care about it's stored gesture value
        String ret, id = "";
        List<Path> foundHmdGesture = gestures.hmdGestures.search(gesture.hmdGesture);
        List<Path> foundRcGesture = gestures.rcGestures.search(gesture.rcGesture);
        List<Path> foundLcGesture = gestures.lcGestures.search(gesture.lcGesture);
        if (foundHmdGesture != null)
            id += foundHmdGesture.hashCode();
        if (foundRcGesture != null)
            id += foundRcGesture.hashCode();
        if (foundLcGesture != null)
            id += foundLcGesture.hashCode();
        System.out.println(gesture);
        System.out.println("foundHmdGesture: " + foundHmdGesture);
        System.out.println("foundRcGesture: " + foundRcGesture);
        System.out.println("foundRcGesture: " + foundLcGesture);
        ret = gestures.gestureNameSpace.get(id);
        return ret;
    }

//    public boolean recognizeTest(VRDataState vrDataWorldPre) {
//        boolean ret = false;
//        for (int i = 0; i < config.GESTURES.length; i++) {
//            Config.SimpleGesture simpleGesture = config.GESTURES[i];
//            if (rcGesture.equals(simpleGesture.rcMovements) && lcGesture.equals(simpleGesture.lcMovements)) {
//                if (trace.rcElapsedTime >= simpleGesture.elapsedTime || trace.lcElapsedTime >= simpleGesture.elapsedTime) {
//                    ret = true; trace.rcMove = trace.lcMove = "";
//                    rcParticle = lcParticle = simpleGesture.particle;
//                    Vec3 avgDir = vrDataWorldPre.getRc()[1].add(vrDataWorldPre.getLc()[1]).multiply((.5), (.5), (.5));
//                    moveParticles(particleTypes[rcParticle],
//                            vrDataWorldPre.getRc()[0],
//                            avgDir,
//                            config.GESTURES[i].velocity
//                    );
//                    moveParticles(particleTypes[lcParticle],
//                            vrDataWorldPre.getLc()[0],
//                            avgDir,
//                            config.GESTURES[i].velocity
//                    );
//                    break;
//                }
//            }
//        }
//        if (!ret) {
//            if (trace.rcMove.equals("forward")) {
//                rcParticle = 0; trace.rcMove = ""; ret = true;
//                Vec3 avgDir = vrDataWorldPre.getRc()[1].add(vrDataWorldPre.getHmd()[1]).multiply((.5), (.5), (.5));
//                moveParticles(particleTypes[rcParticle],
//                        vrDataWorldPre.getRc()[0],
//                        avgDir,
//                        1
//                );
//            }
//            if (trace.lcMove.equals("forward")) {
//                lcParticle = 0; trace.lcMove = ""; ret = true;
//                Vec3 avgDir = vrDataWorldPre.getLc()[1].add(vrDataWorldPre.getHmd()[1]).multiply((.5), (.5), (.5));
//                moveParticles(particleTypes[lcParticle],
//                        vrDataWorldPre.getLc()[0],
//                        avgDir,
//                        1
//                );
//            }
//        }
//        return ret;
//    }

}
