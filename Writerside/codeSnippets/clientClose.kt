// Libère la boucle Gateway, le heartbeat et le client HTTP.
kda.close()
// L'instance ne doit plus être réutilisée après close().
