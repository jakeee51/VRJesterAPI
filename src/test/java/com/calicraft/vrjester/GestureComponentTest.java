package com.calicraft.vrjester;

import com.calicraft.vrjester.gesture.GestureComponent;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import static com.calicraft.vrjester.VrJesterApi.MOD_ID;

@GameTestHolder(MOD_ID)
public class GestureComponentTest {
    @GameTest
    public static void exampleTest(GameTestHelper helper) {
        System.out.println("MADE IT");
        GestureComponent gestureComponent = null;
        helper.succeed();
    }

    // Class name is not prepended, template name is not specified
    // Template Location at 'modid:exampletest2'
    @PrefixGameTestTemplate(false)
    @GameTest
    public static void exampleTest2(GameTestHelper helper) { /*...*/ }
}
