# Commands


## commandExceedChunkMarker

&emsp;Controls the permission level of ECM command. Invalid when ECM is not started.

&emsp;&emsp;- `/exceedChunkMarker - Show current ECM information`

&emsp;&emsp;- `/exceedChunkMarker <dimension> - Show information of the selected dimension`

&emsp;&emsp;- `/exceedChunkMarker [dimension] setTopY <topY> - Set the ECM height of the selected dimension (dimension defaults to the player's current dimension)`

&emsp;&emsp;- `/exceedChunkMarker [dimension] clear - Clear ECM data of the selected dimension (dimension defaults to the player's current dimension)`

&emsp;&emsp;- `/exceedChunkMarker [dimension] loadFromWorld - Load ECM data from world data of the selected dimension (dimension defaults to the player's current dimension)`


## commandLoadedChunkFinder

&emsp;Records connected chunks that are active during a certain period, used to find forgotten chunk loaders.

&emsp;&emsp;- `/loadedChunkFinder - Record chunk loading in the current dimension within 1 tick`

&emsp;&emsp;- `/loadedChunkFinder <dimension> [tick] - Record chunk loading in the specified dimension for a specified duration, tick defaults to 1`


## commandPacketLoggerPlus

&emsp;Records the pre-compression size of various packets.

&emsp;&emsp;- `/packetLogger start - Start recording packets`

&emsp;&emsp;- `/packetLogger stop - Stop recording and display data`

&emsp;&emsp;- `/packetLogger - Show current data (if recording)`


## commandRequirementModify

&emsp;Modify permission requirements of specified commands

&emsp;&emsp;- `/requirementModify <commandPath> <permission> set - Set the permission requirement of the specified command to the given permission`

&emsp;&emsp;- `/requirementModify <commandPath> <permission> add - Add a permission requirement on top of the existing requirements`

&emsp;&emsp;- `/requirementModify <commandPath> <permission> or - Add an OR-condition permission requirement on top of the existing requirements`

&emsp;&emsp;- `/requirementModify <commandPath> clear - Clear permission modifications for the specified command`

&emsp;&emsp;- `/requirementModify clearAll - Clear permission modifications for all commands`

&emsp;&emsp;- `/requirementModify list - List all commands with modified permission requirements`

&emsp;&emsp;- `For commandPath, use <> to indicate parameters:`

&emsp;&emsp;- ` - Example: /requirementModify "player <player> shadow" ops set`

&emsp;&emsp;- ` means setting the permission requirement of "player <player> shadow" to ops`


## commandRulesSearcher

&emsp;Adds a subcommand 'search' to carpet, allowing to search carpet rules by keyword

&emsp;&emsp;- `/carpet search <key> [isNoDefaultValue] [category] - Search carpet rules, key is the search keyword, isNoDefaultValue whether to search only rules with changed default value (default false), category is the rule category`


## commandSpawnWhitedListedPlayer

&emsp;Used to summon fake players without prefix

&emsp;&emsp;- `/player <name> spawn original - summon a fake player without prefix`

