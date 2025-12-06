output "droplet_ip" {
  value = digitalocean_droplet.docker-server.ipv4_address
  description = "IP address of the droplet"
}
