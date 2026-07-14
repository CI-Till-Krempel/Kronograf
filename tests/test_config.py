import os
import unittest
from click.testing import CliRunner
from kronograf.__main__ import main

class TestConfigLoading(unittest.TestCase):
    def setUp(self):
        self.runner = CliRunner()

    def test_no_config_file(self):
        """Test that the CLI exits gracefully if no config file is found."""
        with self.runner.isolated_filesystem():
            result = self.runner.invoke(main, ['run'])
            self.assertNotEqual(result.exit_code, 0)
            self.assertIn("No kronograf.yml found", result.output)

    def test_load_config_with_builtin_plugin(self):
        """Test loading a config that specifies a built-in plugin."""
        with self.runner.isolated_filesystem():
            with open("kronograf.yml", "w") as f:
                f.write("""
plugins:
  - android-kotlin-gradle
""")
            result = self.runner.invoke(main, ['run'])
            self.assertEqual(result.exit_code, 0)
            self.assertIn("Loading built-in plugin: android-kotlin-gradle", result.output)
            self.assertIn("Loaded 1 plugins.", result.output)


    def test_load_config_with_local_plugin(self):
        """Test loading a config that specifies a local plugin."""
        with self.runner.isolated_filesystem():
            # Create kronograf.yml
            with open("kronograf.yml", "w") as f:
                f.write("""
plugins:
  - my-local-plugin
""")
            # Create the local plugin file
            os.makedirs(".kronograf/plugins")
            with open(".kronograf/plugins/my-local-plugin.yml", "w") as f:
                f.write("""
plugin:
  id: my-local-plugin
  name: "My Local Plugin"
""")
            result = self.runner.invoke(main, ['run'])
            self.assertEqual(result.exit_code, 0)
            self.assertIn("Loading local plugin: my-local-plugin", result.output)
            self.assertIn("Loaded 1 plugins.", result.output)

    def test_load_local_plugin_overrides_builtin(self):
        """Test that a local plugin is preferred over a built-in one with the same name."""
        with self.runner.isolated_filesystem():
            # Create kronograf.yml
            with open("kronograf.yml", "w") as f:
                f.write("""
plugins:
  - android-kotlin-gradle # This ID matches a built-in plugin
""")
            # Create a local plugin with the same ID
            os.makedirs(".kronograf/plugins")
            with open(".kronograf/plugins/android-kotlin-gradle.yml", "w") as f:
                f.write("""
plugin:
  id: android-kotlin-gradle
  name: "My LOCAL Android Plugin" # Different name to verify
""")
            result = self.runner.invoke(main, ['run'])
            self.assertEqual(result.exit_code, 0)
            self.assertIn("Loading local plugin: android-kotlin-gradle", result.output)
            self.assertNotIn("Loading built-in plugin", result.output)
            self.assertIn("Loaded 1 plugins.", result.output)

    def test_plugin_not_found(self):
        """Test that a warning is shown for a plugin that cannot be found."""
        with self.runner.isolated_filesystem():
            with open("kronograf.yml", "w") as f:
                f.write("""
plugins:
  - non-existent-plugin
""")
            result = self.runner.invoke(main, ['run'])
            self.assertEqual(result.exit_code, 0) # Should not fail, just warn
            self.assertIn("Plugin 'non-existent-plugin' not found", result.output)
            self.assertIn("Loaded 0 plugins.", result.output)

if __name__ == '__main__':
    unittest.main()
