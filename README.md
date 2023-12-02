VR Jester API
==============

A VR Gesture Recognition API for Vivecraft & QuestCraft.

Play Minecraft VR? Love mods? Ever wish mods were more VR compatible? Wait no further. This is a solution for devs and players alike!

With this API, you may create **any** custom gestures that map to **any** key binds and trigger events.

__Demos:__
https://github.com/jakeee51/VRJesterAPI/assets/19763930/f88e1302-13d8-45bc-a716-3d49585590fe

__Installation:__
1. Install required dependency mods: https://www.curseforge.com/minecraft/mc-mods/vrjesterapi/relations/dependencies 
2. Download the VR Jester API mod & place it in the `mods` folder.
3. Run Minecraft. Necessary config files will generate on initialization.

__Usage:__
1. While in Minecraft VR, bind one of your controller buttons to the "Jester Trigger". I recommend a grip button.
2. These are 2 ways to create gestures:
   - Using the `/gesture` command
   - Modifying the `config/gesture_store.json` directly where gestures are stored
   Sample gesture store:
   ```json
   "GESTURE 1": {
      "RIGHT_CONTROLLER|LEFT_CONTROLLER": [
        {
          "vrDevice": "RIGHT_CONTROLLER|LEFT_CONTROLLER",
          "movement": "forward",
          "elapsedTime": 0,
          "speed": 0.0,
          "direction": {
            "x": 0.0,
            "y": 0.0,
            "z": 0.0
          },
          "devicesInProximity": {}
        }
      ]
    },
   ...
   ```
3. Once your gestures are created, you can map them to key binds in `config/VRJesterAPI.cfg` by creating key value pairs under the field *"GESTURE_MAPPINGS"* like so:
```json
"GESTURE_MAPPINGS": {
    "GESTURE 1": "examplemod.key.ability_1",
    "GESTURE 2": "examplemod.key.ability_2",
    "GESTURE 3": "examplemod.key.ability_3"
}
```

__Info:__
- Consider each object within the square brackets `[]` a mere *piece* of the gesture. You can add multiple of these **GestureComponent** objects. This API recognizes gestures as complex as you want! Just know the more conplex, the more difficult it is to perform a gesture correctly to match your stored gesture.
- Different values for *"movement"* are `forward, back, left, right, up, down`
- Different values for *"vrDevice"* are `RIGHT_CONTROLLER, LEFT_CONTROLLER, HEAD_MOUNTED_DISPLAY` and if you want a gesture recognized on multiple VR devices you can incorporate a logical or using a pipe `|` like so `RIGHT_CONTROLLER|LEFT_CONTROLLER|HEAD_MOUNTED_DISPLAY`
- The *"elapsedTime"* is in milliseconds. So putting a value of `2000` would mean that part of the gesture lasts 2 seconds. This would be good for a charge up sort of move.
- The *"speed"* field is a float value representing the velocity of that GestureComponent. A decent threshold to recognize the speed of a punch would be `1500.0-2000.0` and feel free to play around with the values as much as you want.
- The *"direction"* field takes `x,y,z` float values that create a 3D normalized vector. Example usage of this would be having `{0.0, 1.0, 0.0}` to recognize when the player's brick hand is facing upwards. There's a certain threshold so it doesn't have to be *exact*.
- Finally there's *"devicesInProximity"* which is formatted like so:
  ```json
  "devicesInProximity": {
      "LEFT_CONTROLLER": 20
  }
  ```
  What this example means is that the vrDevice of this GestureComponent has to be within proximity of the left controller for 20 ticks.

__Planned:__
In the future I'll add continous gesture recognition, more configuration options, and optimizations.
