# To learn more about how to use Nix to configure your environment
# see: https://developers.google.com/idx/guides/customize-idx-env
{ pkgs, ... }: {
  # Which nixpkgs channel to use.
  channel = "stable-23.11"; # or "unstable"
  # Use https://search.nixos.org/packages to find packages
  packages = [
    pkgs.zulu21
    pkgs.maven
  ];
  # Sets environment variables in the workspace
  env = {
    JAVA_HOME = "${pkgs.zulu21}";
    LABOR_COST = "15000";
    OPERATING_COST = "7000";
    BENEFIT_PERCENTAGE = "30";
  };
  idx = {
    # Search for the extensions you want on https://open-vsx.org/ and use "publisher.id"
    extensions = [
      "vscjava.vscode-java-pack"
      "google.gemini-cli-vscode-ide-companion"
    ];
    workspace = {
      # Runs when a workspace is first created with this `dev.nix` file
      onCreate = {
        install = "mvn clean install";
      };
      # Runs when a workspace is (re)started
      onStart = {
        # The server will now run on the default port 8080
        run-server = "mvn spring-boot:run";
      };
    };
  };
}
