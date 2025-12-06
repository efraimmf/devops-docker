terraform {
    required_providers {
        digitalocean = {
            source = "digitalocean/digitalocean"
            version = ">=2.69.0"
        }
    }
    cloud {
        organization = "devops_docker"
        
        workspaces {
            name = "devops_docker"
        }
    }
}

provider "digitalocean" {
    token = var.do_token
}

resource "digitalocean_ssh_key" "default" {
    name = "devops-docker"
    public_key = file(var.pub_key)
}

resource "digitalocean_droplet" "docker-server" {
    image = "ubuntu-24-04-x64"
    name = "devops-docker"
    region = "nyc1"
    size = "s-1vcpu-1gb"
    ssh_keys = [digitalocean_ssh_key.default.fingerprint]
    user_data = file("user-data.sh")
    tags = ["terraform", "docker"]
}
