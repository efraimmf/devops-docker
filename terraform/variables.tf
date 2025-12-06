variable "do_token" {
  type = string
  description = "Digital Ocean token"
  sensitive = true
}

variable "pub_key" {
  type = string
  description = "Public key for SSH access"
  default = "./keys/id_ed25519.pub"
}