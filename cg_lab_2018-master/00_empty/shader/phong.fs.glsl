/**
 * a phong shader implementation
 * Created by Samuel Gratzl on 29.02.2016.
 */
precision mediump float;

/**
 * definition of a material structure containing common properties
 */
struct Material {
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	vec4 emission;
	float shininess;
};

/**
 * definition of the light properties related to material properties
 */
struct Light {
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
};

//material uniform
uniform Material u_material;

//light uniforms
uniform Light u_sun;
uniform Light u_lamp1;

//varying vectors for light computation
varying vec3 v_normalVec;
varying vec3 v_eyeVec;
varying vec3 v_lightVec;
varying vec3 v_light2Vec;

//texture variables
varying vec2 v_texCoord;
uniform bool u_enableObjectTexture;
uniform sampler2D u_tex;	//texture unit to use

uniform bool u_enableHeightmap;
varying vec4 v_heightColorDifference;

vec4 calculateSimplePointLight(Light light, Material material, vec3 lightVec, vec3 normalVec, vec3 eyeVec, vec4 textureColor) {
	lightVec = normalize(lightVec);
	normalVec = normalize(normalVec);
	eyeVec = normalize(eyeVec);

	//compute diffuse term
	float diffuse = max(dot(normalVec,lightVec),0.0);

	//compute specular term
	vec3 reflectVec = reflect(-lightVec,normalVec);
	float spec = pow( max( dot(reflectVec, eyeVec), 0.0) , material.shininess);

	if(u_enableObjectTexture)	{
		//replace texture colors with material colors
		material.diffuse = textureColor;
		material.ambient = textureColor * 0.6;
	}

	if(u_enableHeightmap){
		material.diffuse -= v_heightColorDifference;
		material.ambient -= v_heightColorDifference;
	}

	//clamp values
	vec4 c_amb  = clamp(light.ambient  * material.ambient, 0.0, 1.0);
	vec4 c_diff = clamp(diffuse  * light.diffuse * material.diffuse, 0.0, 1.0);
	vec4 c_spec = clamp(spec * light.specular * material.specular, 0.0, 1.0);
	vec4 c_em   = material.emission;

	return c_amb + c_diff + c_spec + c_em;
}

void main() {
	vec4 textureColor = vec4(1,1,1,1);
	textureColor = texture2D(u_tex, v_texCoord);

	gl_FragColor =
		calculateSimplePointLight(u_sun, u_material, v_lightVec, v_normalVec, v_eyeVec, textureColor)
		+ calculateSimplePointLight(u_lamp1, u_material, v_light2Vec, v_normalVec, v_eyeVec, textureColor);
}