import click
import os
import yaml
import pkg_resources

# --- Configuration Loading ---

def find_config_file():
    """Search for kronograf.yml in the current directory and parent directories."""
    current_dir = os.getcwd()
    while current_dir != os.path.dirname(current_dir):  # Stop at root
        config_path = os.path.join(current_dir, "kronograf.yml")
        if os.path.exists(config_path):
            return config_path
        current_dir = os.path.dirname(current_dir)
    return None

def load_config(path):
    """Load and parse the YAML configuration file."""
    if not path:
        return None
    try:
        with open(path, 'r') as f:
            return yaml.safe_load(f)
    except Exception as e:
        click.echo(f"Error loading configuration file at {path}: {e}", err=True)
        return None

def load_plugins(config, repo_root):
    """Load plugins specified in the configuration."""
    if not config or 'plugins' not in config:
        return {}

    loaded_plugins = {}
    plugin_ids = config['plugins']

    for plugin_id in plugin_ids:
        plugin_content = None
        # 1. Check for local plugins first
        local_plugin_path = os.path.join(repo_root, ".kronograf", "plugins", f"{plugin_id}.yml")
        if os.path.exists(local_plugin_path):
            click.echo(f"Loading local plugin: {plugin_id}")
            with open(local_plugin_path, 'r') as f:
                plugin_content = yaml.safe_load(f)
        else:
            # 2. Fall back to built-in plugins
            try:
                resource_path = f"plugins/{plugin_id}.yml"
                if pkg_resources.resource_exists('kronograf', resource_path):
                    click.echo(f"Loading built-in plugin: {plugin_id}")
                    plugin_yaml = pkg_resources.resource_string('kronograf', resource_path)
                    plugin_content = yaml.safe_load(plugin_yaml)
                else:
                    click.echo(f"Warning: Plugin '{plugin_id}' not found.", err=True)
            except Exception as e:
                 click.echo(f"Error loading built-in plugin '{plugin_id}': {e}", err=True)


        if plugin_content:
            loaded_plugins[plugin_id] = plugin_content

    return loaded_plugins

# --- CLI ---

@click.group()
@click.pass_context
def main(ctx):
    """Kronograf: A tool for visualizing technical debt from build artifacts."""
    config_path = find_config_file()
    if not config_path:
        click.echo("No kronograf.yml found in this directory or any parent.", err=True)
        ctx.exit(1)

    repo_root = os.path.dirname(config_path)
    config = load_config(config_path)
    if not config:
        ctx.exit(1)

    plugins = load_plugins(config, repo_root)

    ctx.obj = {
        'repo_root': repo_root,
        'config': config,
        'plugins': plugins
    }
    click.echo(f"Loaded {len(plugins)} plugins.")


@main.command()
@click.pass_context
def run(ctx):
    """Run the Kronograf data extraction and processing pipeline."""
    click.echo("Running Kronograf...")
    # This is where the logic for STOR-6, 7, and 8 will eventually be called.
    click.echo("Extraction, storage, and rendering will happen here.")


if __name__ == '__main__':
    main()
