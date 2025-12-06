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

data "digitalocean_ssh_key" "default" {
    name = "devops-docker"
}

resource "digitalocean_droplet" "docker-server" {
    image = "ubuntu-24-04-x64"
    name = "devops-docker"
    region = "nyc1"
    size = "s-1vcpu-1gb"
    ssh_keys = [data.digitalocean_ssh_key.default.id]
    user_data = file("user-data.sh")
    tags = ["terraform", "docker"]
}
