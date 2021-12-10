package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.gestures.JesterRecognition;
import com.calicraft.vrjester.utils.vrdata.VRDataAggregator;
import com.calicraft.vrjester.utils.vrdata.VRDataWriter;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.Random;

import static com.calicraft.vrjester.VrJesterApi.VIVECRAFTLOADED;
import static com.calicraft.vrjester.VrJesterApi.getMCI;

public class TriggerEventHandler {
    private static final VRDataAggregator data_aggregator = new VRDataAggregator();
    private static final VRDataWriter vrDataWriter = new VRDataWriter(
            "VRJester_Data", new String[]{"rc"});
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static boolean listener = false;
    private long elapsed_time = 0;

    ClientWorld clientWorld; Random rand; Vector3d eyePos, newPos;
    double motionX, motionY,  motionZ, mosX, mosY; Vector2f rotVec;
    // TODO - Set maximum listening time

    @SubscribeEvent
    public void onJesterTrigger(InputEvent.KeyInputEvent event) {
        // Trigger the gesture listening phase
        if (VIVECRAFTLOADED) {
            if (VrJesterApi.MOD_KEY.isDown() && !listener) {
                System.out.println("JESTER TRIGGERED");
                listener = true;
                elapsed_time = System.nanoTime();
                ClientPlayerEntity player = getMCI().player;
                ITextComponent text = new StringTextComponent("Listening for gesture...");
                assert player != null;
                player.sendMessage(text, player.getUUID());
            } else { // Trigger the gesture recognition phase
                System.out.println("JESTER RELEASED");
                listener = false;
                elapsed_time = System.nanoTime() - elapsed_time;
                JesterRecognition recognizer = new JesterRecognition(data_aggregator.getData(), elapsed_time);
                recognizer.isLinearGesture(VRDevice.RC);
                data_aggregator.clear();
                elapsed_time = 0;
                // Fire event or trigger something based on recognized gesture
            }
        } else {
            if (VrJesterApi.MOD_KEY.isDown()) {
                ClientPlayerEntity player = getMCI().player;
                assert player != null;
                eyePos = player.getEyePosition((1f));
                rotVec = player.getRotationVector();
                Vector3d p = player.position();
                mosX = getMCI().mouseHandler.xpos();
                mosY = getMCI().mouseHandler.ypos();
                newPos = sphere(eyePos, rotVec);
                clientWorld = (ClientWorld) player.getCommandSenderWorld();
                for (int i = 0; i < 20; i++) {
                    rand = new Random();
                    motionX = rand.nextGaussian() * 0.0025D;
                    motionY = rand.nextGaussian() * 0.0025D;
                    motionZ = rand.nextGaussian() * 0.0025D;
                    clientWorld.addParticle(ParticleTypes.FLAME,
                            newPos.x, newPos.y, newPos.z,
                            motionX, motionY, motionZ);
//                    clientWorld.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
//                            (eyePos.x), (eyePos.y), (eyePos.z),
//                            motionX, motionY, motionZ);
                }
            }
        }
    }

    private static Vector3d sphere(Vector3d center, Vector2f rotation) {
        double radius = 1F, x, y, z;

        x = center.x + radius * Math.cos(rotation.x) * Math.sin(rotation.y);
        y = center.y + radius * Math.sin(rotation.x) * Math.sin(rotation.y);
        z = center.z + radius * Math.cos(rotation.y);

        return new Vector3d(x, y, z);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) throws IOException {
        // TODO - Attempt to recognize gesture after
        //  certain amount of data captured. Stop listening
        //  after being idle for some time

        // Listen for VR data after trigger
        if (listener) { // Capture data in real time
            data_aggregator.listen();
//            vrDataWriter.write(data_state); // Write data to file(s) to debug/analyze
//            if (sleep % 20 == 0) // Print every 1 second
//                System.out.println("JESTER LISTENING");
//            if (sleep == 0) { // Reset trigger when done
//                System.out.println("JESTER DONE LISTENING");
//                sleep = 2 * DELAY;
//                VRDataAggregator.send(data);
//                data.clear(); listener = false;
//            }
//            sleep--;
        }
    }
}
