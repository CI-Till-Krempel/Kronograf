from setuptools import setup, find_packages

setup(
    name="kronograf",
    version="0.1.0",
    packages=find_packages(),
    include_package_data=True,
    install_requires=[
        "click",
        "PyYAML",
    ],
    entry_points={
        "console_scripts": [
            "kronograf = kronograf.__main__:main",
        ],
    },
    package_data={
        'kronograf': ['plugins/*.yml'],
    }
)
