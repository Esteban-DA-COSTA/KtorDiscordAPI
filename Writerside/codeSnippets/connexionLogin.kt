// Ouvre la connexion Gateway. Renvoie le Job de la boucle de connexion.
val job = kda.login(intents = 1 or 512 or 32768)

// runBlocking { } garde le processus vivant tant que le bot tourne.
