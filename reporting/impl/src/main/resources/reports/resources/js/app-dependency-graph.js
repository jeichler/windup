$(document).ready(function () {

var datasets = [];

function app_dependencies(dataset) {
	datasets.push(dataset);
}

angular.module('exampleApps', [ 'applicationUI' ]).run(
		function($rootScope) {
			var index = 0;
			$rootScope.data = datasets[index];

			// update($rootScope.data)

			$rootScope.kinds = {
				Ear : '#vertex-Ear',
				War : '#vertex-War',
				WarApp : '#vertex-WarApp',
				ExternalJar : '#vertex-ExternalJar',
				Jar : '#vertex-Jar',
			};

			$rootScope.poke = function() {
				index += 1;
				$rootScope.data = datasets[index % datasets.length];
			};

			$rootScope.$on("select",
					function(ev, item) {
						var text = "";
						if (item)
							text = "Selected: " + item.metadata.name
						angular.element(document.getElementById("selected"))
								.text(text);
					});
		});
});