# Apiman Rate Limiting Service Performance Testing

## Running the Perf Test

    mvn gatling:execute -Dgatling.simulationClass=SingleRateLimitSimulation

Possible values for the simulation class:

* SmokeTestSimulation
* SingleRateLimitSimulation
* SeveralRateLimitsSimulation
* UniqueRateLimitsSimulation
* KnockOverSimulation

