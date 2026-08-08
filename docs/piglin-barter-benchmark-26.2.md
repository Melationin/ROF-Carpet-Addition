# Piglin bartering benchmark (Minecraft 26.2)

This benchmark places 270 persistent piglins and 243 item entities containing 64 gold ingots each (15,552 ingots total) in the same one-block bartering cell. Entity cramming is disabled. Each result covers 600 game ticks under `/tick sprint 600t`, with Carpet's `/profile health 600` running over the same window.

| `piglinStackingAISuppression` | Tick time | Effective rate | Gold consumed | Piglins remaining |
| ---: | ---: | ---: | ---: | ---: |
| 10000 (full AI baseline) | 88.95 ms/tick | 11 TPS | 1,350 | 270 |
| 100 | 60.58 ms/tick | 16 TPS | 1,491 | 270 |
| 1 | 44.01 ms/tick | 22 TPS uncapped | 1,412 | 270 |

The limit controls how many piglins per block retain full general-purpose AI. Suppressed piglins still perform vanilla bartering: nearest-item sensing is shared per block and nonessential behavior work is spread across five ticks. The limit of 1 reduced tick time by 50.5% in this stress case without reducing completed barters during the sample.

These are local development-server measurements, not a guarantee for other CPUs, mod packs, or farm layouts. The setup intentionally represents an extreme stacked trading cell and includes the cost of the growing barter-output item population.
